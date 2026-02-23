package pet.project.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Builder
public class PasteViewDto {
    private String paste;
    private String category;
    private String tag;
    private String expiration;
    private String createdAtRelative;
    private String title;
}
