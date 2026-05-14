package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PowerSupply;

import java.util.Optional;

public interface PowerSupplyRepository extends JpaRepository<PowerSupply, Long> {
    Boolean existsByNameIgnoreCase(String name);
    Optional<PowerSupply> findByNameIgnoreCase(String name);
}
