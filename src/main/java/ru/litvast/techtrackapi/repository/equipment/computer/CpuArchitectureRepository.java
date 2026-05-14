package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuArchitecture;
import java.util.Optional;

public interface CpuArchitectureRepository extends JpaRepository<CpuArchitecture, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<CpuArchitecture> findByNameIgnoreCase(String name);
}