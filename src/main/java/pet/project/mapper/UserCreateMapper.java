package pet.project.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pet.project.database.entity.Role;
import pet.project.database.entity.User;
import pet.project.dto.UserDto;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<User, UserDto>{
    private final PasswordEncoder passwordEncoder;

    @Override
    public User mapFrom(UserDto userDto) {
        return User.builder()
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole() != null ? Role.valueOf(userDto.getRole().toUpperCase()) : Role.USER).build();
    }
}
