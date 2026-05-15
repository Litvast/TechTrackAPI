package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.AssignmentHistoryDto;
import ru.litvast.techtrackapi.model.dto.CreateAssignmentDto;
import ru.litvast.techtrackapi.model.dto.ReturnEquipmentDto;
import ru.litvast.techtrackapi.model.dto.mapping.AssignmentHistoryMapping;
import ru.litvast.techtrackapi.model.entity.AssignmentHistory;
import ru.litvast.techtrackapi.model.entity.Employee;
import ru.litvast.techtrackapi.model.entity.equipment.Equipment;
import ru.litvast.techtrackapi.model.entity.equipment.EquipmentStatus;
import ru.litvast.techtrackapi.repository.AssignmentHistoryRepository;
import ru.litvast.techtrackapi.repository.EmployeeRepository;
import ru.litvast.techtrackapi.repository.equipment.EquipmentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignmentHistoryService {

    private final AssignmentHistoryRepository assignmentHistoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AssignmentHistoryMapping assignmentHistoryMapping;

    // CREATE (выдать оборудование сотруднику)
    @Transactional
    public AssignmentHistoryDto assignEquipment(CreateAssignmentDto dto) {
        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Equipment with id '%d' not found", dto.getEquipmentId())
                ));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Employee with id '%d' not found", dto.getEmployeeId())
                ));

        if (assignmentHistoryRepository.findByEquipmentIdAndReturnedAtIsNull(dto.getEquipmentId()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Equipment with id '%d' is already assigned to someone", dto.getEquipmentId())
            );
        }

        equipment.setStatus(EquipmentStatus.ASSIGNED);
        equipmentRepository.save(equipment);

        AssignmentHistory history = assignmentHistoryMapping.toEntity(dto);
        history.setEquipment(equipment);
        history.setEmployee(employee);
        history.setAssignedAt(LocalDateTime.now());
        assignmentHistoryRepository.save(history);
        return assignmentHistoryMapping.toDto(history);
    }

    // RETURN (вернуть оборудование)
    @Transactional
    public AssignmentHistoryDto returnEquipment(ReturnEquipmentDto dto) {
        AssignmentHistory history = assignmentHistoryRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Assignment record with id '%d' not found", dto.getId())
                ));

        if (history.getReturnedAt() != null) {
            throw new IllegalArgumentException("Equipment already returned");
        }

        history.setReturnedAt(LocalDateTime.now());
        if (dto.getCondition() != null) {
            history.setCondition(dto.getCondition());
        }
        assignmentHistoryRepository.save(history);

        boolean hasActiveAssignments = assignmentHistoryRepository
                .findByEquipmentIdAndReturnedAtIsNull(history.getEquipment().getId())
                .isPresent();

        if (!hasActiveAssignments) {
            history.getEquipment().setStatus(EquipmentStatus.IN_STOCK);
            equipmentRepository.save(history.getEquipment());
        }

        return assignmentHistoryMapping.toDto(history);
    }

    // READ all with pagination
    public Page<AssignmentHistoryDto> getAllAssignments(Pageable pageable) {
        Page<AssignmentHistory> histories = assignmentHistoryRepository.findAll(pageable);
        if (histories.isEmpty()) {
            throw new NoEntitiesFoundException("No assignment records found");
        }
        return histories.map(assignmentHistoryMapping::toDto);
    }

    // READ by equipment id
    public Page<AssignmentHistoryDto> getAssignmentsByEquipmentId(Long equipmentId, Pageable pageable) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new EntityNotFoundException(
                    String.format("Equipment with id '%d' not found", equipmentId)
            );
        }

        Page<AssignmentHistory> histories = assignmentHistoryRepository.findByEquipmentId(equipmentId, pageable);
        if (histories.isEmpty()) {
            throw new NoEntitiesFoundException("No assignment records found for this equipment");
        }
        return histories.map(assignmentHistoryMapping::toDto);
    }

    // READ by employee id
    public Page<AssignmentHistoryDto> getAssignmentsByEmployeeId(Long employeeId, Pageable pageable) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException(
                    String.format("Employee with id '%d' not found", employeeId)
            );
        }

        Page<AssignmentHistory> histories = assignmentHistoryRepository.findByEmployeeId(employeeId, pageable);
        if (histories.isEmpty()) {
            throw new NoEntitiesFoundException("No assignment records found for this employee");
        }
        return histories.map(assignmentHistoryMapping::toDto);
    }

    // READ by id
    public AssignmentHistoryDto getAssignmentById(Long id) {
        AssignmentHistory history = assignmentHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Assignment record with id '%d' not found", id)
                ));
        return assignmentHistoryMapping.toDto(history);
    }

    // READ current assignment by equipment id
    public AssignmentHistoryDto getCurrentAssignmentByEquipmentId(Long equipmentId) {
        AssignmentHistory history = assignmentHistoryRepository.findByEquipmentIdAndReturnedAtIsNull(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("No active assignment found for equipment with id '%d'", equipmentId)
                ));
        return assignmentHistoryMapping.toDto(history);
    }
}