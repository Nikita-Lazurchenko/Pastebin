package pet.project.dto;

import lombok.Data;

@Data
public class ChangeUserPasswordDto {
    private String username;
    private String password;
}
