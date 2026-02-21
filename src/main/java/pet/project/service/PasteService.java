package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.database.repository.UserRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.dto.PasteViewDto;
import pet.project.mapper.PasteCreateMapper;
import pet.project.mapper.PasteViewMapper;
import pet.project.storage.GoogleDrive;
import pet.project.storage.PasteFile;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PasteService {
    private final PasteRepository pasteRepository;
    private final UserRepository userRepository;
    private final PasteCreateMapper pasteCreateMapper;
    private final PasteViewMapper pasteViewMapper;
    private final GoogleDrive googleDrive;

    @Transactional
    public Paste save(PasteCreateDto pasteCreateDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Paste paste = pasteCreateMapper.mapFrom(pasteCreateDto);
        paste.setUser(user);

        Optional<PasteFile> pasteFile = googleDrive.uploadFileToDrive(pasteCreateDto.getPaste());
        paste.setPasteLink(pasteFile.orElseThrow().pasteId());
        paste.setGoogleFileId(pasteFile.get().FileId());

        return pasteRepository.save(paste);
    }

    public PasteViewDto findByHash(String hash) {
        Paste paste = pasteRepository.findByHash(hash);

        PasteViewDto pasteViewDto = pasteViewMapper.mapFrom(paste);
        pasteViewDto.setPaste(googleDrive.downloadFileFromDrive(paste.getGoogleFileId()).orElseThrow());

        return pasteViewDto;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void deletedAllExpiredPastes(){
        List<String> googleDriveFileIds = pasteRepository.deleteAllExpiredPastesAndReturnFileId();

        if(!googleDriveFileIds.isEmpty()){
            googleDriveFileIds.forEach(googleDrive::deleteFileFromDrive);
        }

        System.out.println("Произошла очистка Pastes");
    }
}
