package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.mapper.PasteCreateMapper;
import pet.project.mapper.PasteViewMapper;
import pet.project.storage.GoogleDrive;
import pet.project.storage.PasteFile;

import java.util.*;


@Service
@RequiredArgsConstructor
public class PasteService {
    private final PasteRepository pasteRepository;
    private final UserService userService;
    private final PasteCreateMapper pasteCreateMapper;
    private final PasteViewMapper pasteViewMapper;
    private final GoogleDrive googleDrive;
    private final RedisTemplate<String, Long> redisPasteViews;

    @Transactional
    public Paste save(PasteCreateDto pasteCreateDto, Long userId) {
        User user = userService.findById(userId);

        Paste paste = pasteCreateMapper.mapFrom(pasteCreateDto);
        paste.setUser(user);

        Optional<PasteFile> pasteFile = googleDrive.uploadFileToDrive(pasteCreateDto.getPaste());
        paste.setPasteLink(pasteFile.orElseThrow().pasteId());
        paste.setGoogleFileId(pasteFile.get().FileId());

        return pasteRepository.save(paste);
    }

    @Transactional
    public PasteViewDto findByHash(String hash) {
        Paste paste = pasteRepository.findByHash(hash);

        PasteViewDto pasteViewDto = pasteViewMapper.mapFrom(paste);
        pasteViewDto.setPaste(googleDrive.downloadFileFromDrive(paste.getGoogleFileId()).orElseThrow());

        Long currentRedisViews = redisPasteViews.opsForValue().increment("views:" + hash);

        String totalViews = String.valueOf((paste.getViews() != null ? paste.getViews() : 0) + (currentRedisViews != null ? currentRedisViews : 0));
        pasteViewDto.setViews(totalViews);

        return pasteViewDto;
    }

    @Scheduled(fixedDelay = 330000)
    public void synchronizeViews() {
        Set<String> keys = redisPasteViews.keys("views:*");
        if (keys == null || keys.isEmpty()) return;

        Map<String, Long> viewsMap = new HashMap<>();

        for (String key : keys) {
            Long views = redisPasteViews.opsForValue().get(key);

            if (views != null && views > 0) {
                viewsMap.put(key.substring(6), views);
            }
        }

        if (!viewsMap.isEmpty()) {
            try {
                pasteRepository.updateViewsBatch(viewsMap);

                viewsMap.forEach((hash, processedViews) -> {
                    redisPasteViews.opsForValue().decrement("views:" + hash, processedViews);
                });

            } catch (Exception e) {
            }
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 60 * 1000)
    public void deletedAllExpiredPastes(){
        List<String> googleDriveFileIds = pasteRepository.deleteAllExpiredPastesAndReturnFileId();

        if(!googleDriveFileIds.isEmpty()){
            googleDriveFileIds.forEach(googleDrive::deleteFileFromDrive);
        }

        System.out.println("Произошла очистка Pastes");
    }
}
