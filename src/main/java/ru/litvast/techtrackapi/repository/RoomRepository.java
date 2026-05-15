package ru.litvast.techtrackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.Room;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Room> findByNameIgnoreCase(String name);
    Page<Room> findByBuildingFloorId(Long buildingFloorId, Pageable pageable);
}