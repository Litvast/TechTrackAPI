package ru.litvast.techtrackapi.repository.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.Router;
import java.util.Optional;

public interface RouterRepository extends JpaRepository<Router, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Router> findByNameIgnoreCase(String name);
    boolean existsByInventoryNumber(String inventoryNumber);
    Optional<Router> findByInventoryNumber(String inventoryNumber);
}