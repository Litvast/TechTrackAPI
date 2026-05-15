package ru.litvast.techtrackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.RoomEquipment;

public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, Long> {
    Page<RoomEquipment> findByRoomId(Long roomId, Pageable pageable);
    Page<RoomEquipment> findByEquipmentId(Long equipmentId, Pageable pageable);
    boolean existsByRoomIdAndEquipmentId(Long roomId, Long equipmentId);
}