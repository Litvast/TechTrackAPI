package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.model.entity.equipment.EquipmentStatus;
import ru.litvast.techtrackapi.repository.equipment.EquipmentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    // COUNT by status
    public long countEquipmentsByStatus(EquipmentStatus equipmentStatus) {
        log.debug("Подсчёт количества оборудования по статусу: {}", equipmentStatus);

        return equipmentRepository.countByStatus(equipmentStatus);
    }
}
