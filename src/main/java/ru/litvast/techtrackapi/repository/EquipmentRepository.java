package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.equipment.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}