package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;

public interface RamRepository extends JpaRepository<Ram, Long> {
    Boolean existsByNameIgnoreCase(String name);
}
