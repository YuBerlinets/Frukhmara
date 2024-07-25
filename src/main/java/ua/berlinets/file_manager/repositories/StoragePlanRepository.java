package ua.berlinets.file_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.berlinets.file_manager.entities.StoragePlan;

import java.util.List;
import java.util.Optional;

public interface StoragePlanRepository extends JpaRepository<StoragePlan, Integer> {
    List<StoragePlan> findAll();
    Optional<StoragePlan> findByNameContainingIgnoreCase(String name);
}
