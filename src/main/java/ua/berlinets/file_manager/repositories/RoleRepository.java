package ua.berlinets.file_manager.repositories;

import org.springframework.data.repository.CrudRepository;
import ua.berlinets.file_manager.entities.Role;
import ua.berlinets.file_manager.enums.RoleEnum;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    List<Role> findAll();

    Optional<Role> findByRoleName(RoleEnum roleName);
}
