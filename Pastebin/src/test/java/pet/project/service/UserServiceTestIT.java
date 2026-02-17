package pet.project.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import pet.project.PastebinApplication;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor
@SpringBootTest(classes = PastebinApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserServiceTestIT {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserDto userDto;

    @Test
    public void checkSaveUser(){
        userDto.setFirstname("Ivan");
        userDto.setLastname("Ivanov");
        userDto.setEmail("ivanov@gmail.com");
        userDto.setPassword("password");
        userDto.setRole("USER");

        User savedUser = userService.save(userDto);

        assertNotNull(savedUser.getId());
        assertEquals("Ivan", savedUser.getFirstname());

        assertTrue(userRepository.findById(savedUser.getId()).isPresent());
    }
}
