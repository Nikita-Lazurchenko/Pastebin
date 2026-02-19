package pet.project.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import pet.project.PastebinApplication;
import pet.project.database.entity.Paste;
import pet.project.database.entity.Role;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.database.repository.UserRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.mapper.PasteCreateMapper;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor
@SpringBootTest(classes = PastebinApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PasteServiceTestIT {
    private final PasteRepository pasteRepository;
    private final PasteCreateMapper pasteCreateMapper;
    private final UserRepository userRepository;
    private final PasteCreateDto pasteCreateDto;

    @Test
    public void checkPasteSave(){
        User user = User.builder()
                .firstname("Ivan")
                .lastname("Petrov")
                .email("ivan.petrov@gmail.com")
                .password("password")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        pasteCreateDto.setPaste("text");
        pasteCreateDto.setCategory("Code");
        pasteCreateDto.setTag("#java #spring");
        pasteCreateDto.setExpiration("10 Minutes");
        pasteCreateDto.setAccess("public");
        pasteCreateDto.setPassword("secret");
        pasteCreateDto.setTitle("My Paste");

        Paste paste = pasteCreateMapper.mapFrom(pasteCreateDto);
        paste.setUser(user);

        Paste savedPaste = pasteRepository.save(paste);

        assertNotNull(savedPaste);
        assertNotNull(paste.getId());
    }

}
