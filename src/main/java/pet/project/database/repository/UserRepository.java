package pet.project.database.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pet.project.database.entity.User;

import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager entityManager;

    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    public Optional<User> findById(Long id)
    {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<User> loadUserByUsername(String username){
        return entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username",username)
                .getResultStream()
                .findFirst();
    }

    public int updateUserPassword(String password, String username){
        return  entityManager.createQuery("UPDATE User SET password = :password WHERE username = :username")
                .setParameter("password", password)
                .setParameter("username", username)
                .executeUpdate();
    }
}
