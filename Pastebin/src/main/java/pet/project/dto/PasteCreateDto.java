package pet.project.dto;

import lombok.Data;

@Data
public class PasteCreateDto {
    private String paste;
    private String category;
    private String tag;
    private String expiration;
    private String access;
    private String hasPassOrNo;
    private String password;
    private String title;
}
