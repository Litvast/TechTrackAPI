package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.Building;

public interface BuildingRepository extends JpaRepository<Building, Long> {
}
