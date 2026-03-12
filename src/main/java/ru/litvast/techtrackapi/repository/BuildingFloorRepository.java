package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.BuildingFloor;

public interface BuildingFloorRepository extends JpaRepository<BuildingFloor, Long> {
}