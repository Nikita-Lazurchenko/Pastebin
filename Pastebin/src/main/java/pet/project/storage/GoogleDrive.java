package pet.project.storage;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


@Service
@RequiredArgsConstructor
public class GoogleDrive{
    private final GeneratorHash generatorHash;
    private final Drive drive;
    @Value("${spring.storage.folder-id}")
    private String folderId;
    @Value("${spring.storage.main-path}")
    private String mainPath;

    public String uploadFileToDrive(String textContent) {
        System.out.println(folderId);
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
            String fileUrl = mainPath + fileId;

            System.out.println("FILE URL: " + fileUrl);

            return fileName;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
