package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.RoomEquipment;

public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, Long> {
}