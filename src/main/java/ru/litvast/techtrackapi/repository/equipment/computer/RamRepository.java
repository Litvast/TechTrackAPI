package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;

import java.util.Optional;

public interface RamRepository extends JpaRepository<Ram, Long> {
    Boolean existsByNameIgnoreCase(String name);
    Optional<Ram> findByNameIgnoreCase(String name);
}
