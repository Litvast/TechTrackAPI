package ru.litvast.techtrackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.Building;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Building> findByNameIgnoreCase(String name);
    Page<Building> findByCompanyId(Long companyId, Pageable pageable);
}