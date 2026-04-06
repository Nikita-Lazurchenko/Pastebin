package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.User;

import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Long> getUserIdByUsername(String username){
        return entityManager.createQuery(
                        "SELECT u.id FROM User u WHERE u.username = :username",
                        Long.class)
                .setParameter("username",username)
                .getResultStream()
                .findFirst();
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username){
        return entityManager.createQuery(
                "SELECT u.id FROM User u WHERE u.username = :username",
                        User.class)
                .setParameter("username",username)
                .getResultStream()
                .findFirst();
    }

    @Transactional
    public int updateUserPassword(String password, String username){
        return  entityManager.createQuery("UPDATE User SET password = :password WHERE username = :username")
                .setParameter("password", password)
                .setParameter("username", username)
                .executeUpdate();
    }

    @Transactional
    public void refreshAllUserRatings() {
        Double avgAllPastes = jdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(views), 1.0) FROM pastebin.pastes", Double.class);

        String sql = """
         WITH UserStats AS (
            SELECT 
                user_id,
                SUM(views) as total_views,
                AVG(views) as avg_user_views,
                EXTRACT(EPOCH FROM (NOW() - MAX(created_at))) / 86400 as days_diff
            FROM pastebin.pastes
            GROUP BY user_id
        ),
        CalculatedRatings AS (
            SELECT
                user_id,
                LEAST(5.0,
                    LOG10(total_views + 1) *
                    (1.0 + (avg_user_views / NULLIF(?, 0))) * 0.5 *
                    EXP(-days_diff / 30.0)
                ) as new_rating
            FROM UserStats
                        )
        UPDATE pastebin.users u
        SET rating = cr.new_rating
        FROM CalculatedRatings cr
        WHERE u.id = cr.user_id 
          AND (u.rating IS DISTINCT FROM cr.new_rating);
        """;

        jdbcTemplate.update(sql, avgAllPastes);
    }
}
