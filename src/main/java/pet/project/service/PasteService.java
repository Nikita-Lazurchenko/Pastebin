package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.exception.PasteNotFoundException;
import pet.project.mapper.PasteCreateMapper;
import pet.project.mapper.PasteViewMapper;
import pet.project.storage.GoogleDrive;
import pet.project.storage.PasteFile;

import java.util.*;


@Service
@Slf4j
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

        PasteFile pasteFile = googleDrive.uploadFileToDrive(pasteCreateDto.getPaste())
                .orElseThrow(() -> new RuntimeException("Failed to upload file to Google Drive"));
        paste.setPasteLink(pasteFile.pasteId());
        paste.setGoogleFileId(pasteFile.fileId());

        try {
            return pasteRepository.save(paste);
        } catch (Exception e) {
            log.error("DB error, deleting file from Drive: {}", paste.getGoogleFileId());
            googleDrive.deleteFileFromDrive(paste.getGoogleFileId());
            throw new RuntimeException("Could not save paste, cleaning up cloud storage", e);
        }
    }

    @Transactional
    public PasteViewDto findByHash(String hash) {
        Paste paste;
        try {
            paste = pasteRepository.findByHash(hash).orElseThrow();
        }catch (Exception e){
            log.info("Paste with hash {} not found", hash);
            throw new PasteNotFoundException("Paste not found %s".formatted(hash));
        }

        PasteViewDto pasteViewDto = pasteViewMapper.mapFrom(paste);
        pasteViewDto.setPaste(googleDrive.downloadFileFromDrive(paste.getGoogleFileId()).orElseThrow());

        Long currentRedisViews = redisPasteViews.opsForValue().increment("views:" + hash);

        String totalViews = String.valueOf((paste.getViews() != null ? paste.getViews() : 0) + (currentRedisViews != null ? currentRedisViews : 0));
        pasteViewDto.setViews(totalViews);

        return pasteViewDto;
    }

    @Scheduled(fixedDelay = 330000)
    public void synchronizeViews() {
        ScanOptions options = ScanOptions.scanOptions().match("views:*").count(100).build();

        try (Cursor<String> cursor = redisPasteViews.scan(options)) {
            Map<String, Long> viewsMap = new HashMap<>();

            while (cursor.hasNext()) {
                String key = cursor.next();
                Long views = redisPasteViews.opsForValue().get(key);

                if (views != null && views > 0) {
                    viewsMap.put(key.substring(6), views);
                }
            }

            if (!viewsMap.isEmpty()) {
                log.info("Starting sync of {} items", viewsMap.size());
                try {
                    pasteRepository.updateViewsBatch(viewsMap);

                    viewsMap.forEach((hash, processedViews) -> {
                        redisPasteViews.opsForValue().decrement("views:" + hash, processedViews);
                    });

                    log.info("Sync finished successfully");
                } catch (Exception e) {
                    log.error("Failed to sync views to DB", e);
                }
            }
        } catch (Exception e) {
            log.error("Error during Redis scan", e);
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 60 * 1000)
    public void deletedAllExpiredPastes(){
        List<String> googleDriveFileIds = pasteRepository.deleteAllExpiredPastesAndReturnFileId();

        if(!googleDriveFileIds.isEmpty()){
            googleDriveFileIds.forEach(googleDrive::deleteFileFromDrive);
        }

        log.info("Expired pastes were cleared");
    }

    @Transactional
    public List<PasteViewDto> getPublicPastes(int pageNumber, int pageSize) {
        List<Paste> pastes = pasteRepository.getPublicPastes(pageNumber, pageSize);

        return pastes.stream()
                .map(pasteViewMapper::mapFrom)
                .toList();
    }

    @Transactional
    public List<PasteViewDto> getAuthorPastes(Long userId, int pageNumber, int pageSize) {
        List<Paste> pastes = pasteRepository.getAuthorPastes(userId, pageNumber, pageSize);

        return pastes.stream()
                .map(pasteViewMapper::mapFrom)
                .toList();
    }
}
