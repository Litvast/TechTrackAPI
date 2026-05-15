package ru.litvast.techtrackapi.repository.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.Printer;
import java.util.Optional;

public interface PrinterRepository extends JpaRepository<Printer, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Printer> findByNameIgnoreCase(String name);
    boolean existsByInventoryNumber(String inventoryNumber);
    Optional<Printer> findByInventoryNumber(String inventoryNumber);
}