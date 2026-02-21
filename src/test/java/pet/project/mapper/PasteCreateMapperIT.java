package pet.project.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import pet.project.PastebinApplication;
import pet.project.database.entity.Access;
import pet.project.database.entity.Category;
import pet.project.database.entity.Paste;
import pet.project.dto.PasteCreateDto;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@SpringBootTest(classes = PastebinApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PasteCreateMapperIT {
    private final PasteCreateDto pasteCreateDto;
    private final PasteCreateMapper pasteCreateMapper;

    @Test
    public void checkMapFrom() {
        pasteCreateDto.setPaste("text");
        pasteCreateDto.setCategory("Code");
        pasteCreateDto.setTag("#java #spring");
        pasteCreateDto.setExpiration("10 Minutes");
        pasteCreateDto.setAccess("public");
        pasteCreateDto.setPassword("secret");
        pasteCreateDto.setTitle("My Paste");

        Paste paste = pasteCreateMapper.mapFrom(pasteCreateDto);

        System.out.println(paste);

        assertNotNull(paste);
        assertEquals(Category.CODE, paste.getCategory());
        assertTrue(paste.getTags().containsAll(Set.of("#java", "#spring")));
        assertEquals("My Paste", paste.getTitle());
        assertEquals("secret", paste.getPassword());
        assertEquals(Access.PUBLIC, paste.getAccess());
        assertNotNull(paste.getDeletedAt());
    }

}
