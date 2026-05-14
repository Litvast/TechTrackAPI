package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuSocket;
import java.util.Optional;

public interface CpuSocketRepository extends JpaRepository<CpuSocket, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<CpuSocket> findByNameIgnoreCase(String name);
}