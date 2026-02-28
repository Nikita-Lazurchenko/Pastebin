package pet.project.dto;

import lombok.Data;

@Data
public class UserDto {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String role;
}
