package pet.project.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PasteDto {
    private String paste;
    private String category;
    private String tag;
    private String expiration;
    private String access;
    private String hasPassOrNo;
    private String password;
    private String title;
}
