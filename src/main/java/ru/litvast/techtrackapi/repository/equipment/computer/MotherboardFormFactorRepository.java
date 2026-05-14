package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.MotherboardFormFactor;
import java.util.Optional;

public interface MotherboardFormFactorRepository extends JpaRepository<MotherboardFormFactor, Long> {
    boolean existsByCodeIgnoreCase(String code);
    Optional<MotherboardFormFactor> findByCodeIgnoreCase(String code);
}