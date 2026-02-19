package pet.project.storage;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GoogleDrive{
    private final GeneratorHash generatorHash;
    private final Drive drive;
    @Value("${spring.storage.folder-id}")
    private String folderId;

    public Optional<PasteFile> uploadFileToDrive(String textContent) {
        String fileName = generatorHash.getHash();

        try {
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(fileName + ".txt");
            fileMetaData.setParents(Collections.singletonList(folderId));

            byte[] bytes = textContent.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            InputStreamContent mediaContent = new InputStreamContent("text/plain", inputStream);
            mediaContent.setLength(bytes.length);

            com.google.api.services.drive.model.File uploadedFile = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id")
                    .setSupportsAllDrives(true)
                    .execute();

            String fileId = uploadedFile.getId();

            return Optional.of(new PasteFile(fileName, fileId));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> downloadFileFromDrive(String fileId) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            drive.files().get(fileId)
                    .setSupportsAllDrives(true)
                    .executeMediaAndDownloadTo(outputStream);

            return Optional.of(new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Ошибка при скачивании: " + e.getMessage());
            return Optional.empty();
        }
    }
}
