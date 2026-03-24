package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.Access;
import pet.project.database.entity.Paste;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class PasteRepository {
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

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

    @Transactional
    public void updateViewsBatch(Map<String, Long> viewsMap) {
        String sql = "UPDATE paste SET views = COALESCE(views, 0) + ? WHERE paste_link = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            final List<String> hashes = new ArrayList<>(viewsMap.keySet());

            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                String hash = hashes.get(i);
                preparedStatement.setLong(1, viewsMap.get(hash));
                preparedStatement.setString(2, hash);
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    @Transactional
    public List<Paste> getFivePastes() {
        String jpql = "SELECT p FROM Paste p WHERE p.access = :access ORDER BY p.createdAt DESC";

        return entityManager.createQuery(jpql,Paste.class)
                .setParameter("access", Access.PUBLIC)
                .setMaxResults(5)
                .getResultList();
    }

    @Transactional
    public List<Paste> getFiveAuthorPastes(Long userId) {
        String jpql = "SELECT p FROM Paste p WHERE p.user.id = :userId ORDER BY p.createdAt DESC";

        return entityManager.createQuery(jpql,Paste.class)
                .setParameter("userId", userId)
                .setMaxResults(5)
                .getResultList();
    }
}
