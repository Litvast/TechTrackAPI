package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Motherboard;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {
    Boolean existsByNameIgnoreCase(String name);
}
