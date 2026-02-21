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

    public Optional<User> findById(Long id){
        return Optional.ofNullable(entityManager.find(User.class, id));
    }
}
