package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.Paste;

@RequiredArgsConstructor
@Repository
public class PasteRepository {
    private final EntityManager entityManager;

    @Transactional
    public Paste save(Paste paste) {
        if (paste.getId() == null) {
            System.out.println(paste);
            entityManager.persist(paste);
            return paste;
        } else {
            return entityManager.merge(paste);
        }
    }

}
