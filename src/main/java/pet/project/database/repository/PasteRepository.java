package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.Paste;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PasteRepository {
    private final EntityManager entityManager;

    @Transactional
    public Paste save(Paste paste) {
        if (paste.getId() == null) {
            entityManager.persist(paste);
            return paste;
        } else {
            return entityManager.merge(paste);
        }
    }

    public Paste findByHash(String hash) {
        return entityManager.createQuery(
                        "SELECT p FROM Paste p WHERE p.pasteLink = :hash", Paste.class)
                .setParameter("hash", hash)
                .getSingleResult();
    }

    @Transactional
    public List<String> deleteAllExpiredPastesAndReturnFileId() {
        LocalDateTime now = LocalDateTime.now();

        List<String> deletedGoogleFileId = entityManager
                .createQuery("SELECT p.googleFileId FROM Paste p WHERE p.deletedAt < :now", String.class)
                .setParameter("now", now)
                .getResultList();

        if (!deletedGoogleFileId.isEmpty()) {
            entityManager.createQuery("DELETE FROM Paste p WHERE p.deletedAt < :now")
                    .setParameter("now", now)
                    .executeUpdate();
        }

        return deletedGoogleFileId;
    }

}
