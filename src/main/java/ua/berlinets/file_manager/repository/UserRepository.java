package ua.berlinets.file_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.berlinets.file_manager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);
}
