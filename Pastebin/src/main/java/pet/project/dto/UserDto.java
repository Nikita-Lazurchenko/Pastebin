package pet.project.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
}
