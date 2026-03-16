package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pet.project.database.entity.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasteRepository pasteRepository;

    @Autowired
    private EntityManager entityManager;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:18.3"));

    @DynamicPropertySource
    static void configurationProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        registry.add("spring.datasource.hikari.connection-init-sql", () -> "CREATE SCHEMA IF NOT EXISTS pastebin");
    }

    @Test
    public void testUpdateUserRating(){
        User popularUser = new User();
        popularUser.setFirstname("Ivan");
        popularUser.setLastname("Ivanov");
        popularUser.setUsername("popular");
        popularUser.setEmail("pop@test.com");
        popularUser.setPassword("123");
        popularUser.setRole(Role.USER);
        userRepository.save(popularUser);

        Paste p1 = createPaste(popularUser, "link1", "file1", 100L, LocalDateTime.now().minusDays(7));
        pasteRepository.save(p1);
        Paste p2 = createPaste(popularUser, "link2", "file2", 155L, LocalDateTime.now());
        pasteRepository.save(p2);

        User inactiveUser = new User();
        inactiveUser.setFirstname("Petr");
        inactiveUser.setLastname("Petrov");
        inactiveUser.setUsername("inactive");
        inactiveUser.setEmail("old@test.com");
        inactiveUser.setPassword("123");
        inactiveUser.setRole(Role.USER);
        userRepository.save(inactiveUser);

        Paste p3 = createPaste(inactiveUser, "link3", "file3", 200L, LocalDateTime.now().minusDays(7));
        pasteRepository.save(p3);

        userRepository.refreshAllUserRatings();

        entityManager.clear();

        User updatedPop = userRepository.findById(popularUser.getId()).orElseThrow();
        User updatedInactive = userRepository.findById(inactiveUser.getId()).orElseThrow();

        System.out.println("Pop rating: " + updatedPop.getRating());
        System.out.println("Inactive rating: " + updatedInactive.getRating());

        assertAll(
                () -> assertTrue(updatedPop.getRating() > 0, "Рейтинг должен быть больше 0"),
                () -> assertTrue(updatedPop.getRating() <= 5.0, "Рейтинг не может превышать 5"),
                () -> assertTrue(updatedPop.getRating() > updatedInactive.getRating(),
                        "Свежий контент должен цениться выше старого")
        );
    }

    private Paste createPaste(User user, String link, String fileId, Long views, LocalDateTime createdAt) {
        Paste paste = new Paste();
        paste.setUser(user);
        paste.setPasteLink(link);
        paste.setGoogleFileId(fileId);
        paste.setViews(views);
        paste.setCreatedAt(createdAt);

        paste.setCategory(Category.CODE);
        paste.setAccess(Access.PUBLIC);
        paste.setExpiration(Expiration.NEVER);
        paste.setTitle("Test Title");
        paste.setDeletedAt(createdAt.plusDays(7));
        return paste;
    }
}