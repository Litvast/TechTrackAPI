package ru.litvast.techtrackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.BuildingFloor;
import java.util.Optional;

public interface BuildingFloorRepository extends JpaRepository<BuildingFloor, Long> {
    boolean existsByFloorNumberAndBuildingId(Integer floorNumber, Long buildingId);
    Page<BuildingFloor> findByBuildingId(Long buildingId, Pageable pageable);
    Optional<BuildingFloor> findByFloorNumberAndBuildingId(Integer floorNumber, Long buildingId);
}