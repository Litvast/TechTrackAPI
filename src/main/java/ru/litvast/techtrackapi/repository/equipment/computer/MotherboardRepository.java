package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Motherboard;
import java.util.Optional;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Motherboard> findByNameIgnoreCase(String name);
}