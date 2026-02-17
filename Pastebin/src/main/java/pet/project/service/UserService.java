package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pet.project.database.entity.Role;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.UserDto;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User save(UserDto userDto) {
        User user = User.builder()
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .role(Role.valueOf(userDto.getRole().toUpperCase())).build();

        return userRepository.save(user);
    }
}
