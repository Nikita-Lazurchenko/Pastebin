package pet.project.configurations;

import com.google.api.services.drive.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pet.project.storage.DriveService;

@Configuration
public class GoogleDriveConfig {
    @Bean
    public Drive drive() {
        return DriveService.createDriveService();
    }
}
