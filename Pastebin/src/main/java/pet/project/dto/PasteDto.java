package pet.project.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

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
