package ua.berlinets.file_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<ua.berlinets.file_manager.entities.User, String> {
    Optional<ua.berlinets.file_manager.entities.User> findByUsername(String username);
    List<ua.berlinets.file_manager.entities.User> findAllByAccountIsConfirmed(boolean accountIsConfirmed);
    void deleteByUsername(String username);
}
