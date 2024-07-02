package ua.berlinets.file_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.berlinets.file_manager.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    List<User> findAllByAccountIsConfirmed(boolean accountIsConfirmed);

    void deleteByUsername(String username);

    Optional<User> findByRefreshToken(String refreshToken);
}
