package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Processor;

import java.util.Optional;

public interface ProcessorRepository extends JpaRepository<Processor, Long> {
    Boolean existsByNameIgnoreCase(String name);
    Optional<Processor> findByNameIgnoreCase(String name);
}
