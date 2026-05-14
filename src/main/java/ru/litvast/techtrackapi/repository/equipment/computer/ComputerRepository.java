package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;

import java.util.Optional;

public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Boolean existsByNameIgnoreCase(String name);
    Optional<Computer> findByNameIgnoreCase(String name);
}