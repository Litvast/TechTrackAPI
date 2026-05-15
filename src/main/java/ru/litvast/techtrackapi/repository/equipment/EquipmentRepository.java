package ru.litvast.techtrackapi.repository.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.Equipment;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Equipment> findByNameIgnoreCase(String name);
    boolean existsByInventoryNumber(String inventoryNumber);
    Optional<Equipment> findByInventoryNumber(String inventoryNumber);
}