package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;

public interface ComputerRepository extends JpaRepository<Computer, Long> {
}