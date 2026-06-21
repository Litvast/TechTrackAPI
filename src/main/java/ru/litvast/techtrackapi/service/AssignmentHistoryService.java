package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentHistoryService {

    private final AssignmentHistoryRepository assignmentHistoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AssignmentHistoryMapping assignmentHistoryMapping;

    @Transactional
    public AssignmentHistoryDto assignEquipment(CreateAssignmentDto dto) {
        log.info("=== НАЧАЛО: Выдача оборудования ===");
        log.info("Оборудование ID: {}, Сотрудник ID: {}", dto.getEquipmentId(), dto.getEmployeeId());

        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> {
                    log.error("Оборудование с ID {} не найдено", dto.getEquipmentId());
                    return new EntityNotFoundException(
                            String.format("Equipment with id '%d' not found", dto.getEquipmentId())
                    );
                });

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> {
                    log.error("Сотрудник с ID {} не найден", dto.getEmployeeId());
                    return new EntityNotFoundException(
                            String.format("Employee with id '%d' not found", dto.getEmployeeId())
                    );
                });

        log.info("Оборудование: {}, Сотрудник: {}", equipment.getName(), employee.getFullName());

        if (assignmentHistoryRepository.findByEquipmentIdAndReturnedAtIsNull(dto.getEquipmentId()).isPresent()) {
            log.warn("Оборудование ID {} уже выдано другому сотруднику", dto.getEquipmentId());
            throw new IllegalArgumentException(
                    String.format("Equipment with id '%d' is already assigned to someone", dto.getEquipmentId())
            );
        }

        equipment.setStatus(EquipmentStatus.ASSIGNED);
        equipmentRepository.save(equipment);
        log.info("Статус оборудования изменён на ASSIGNED");

        AssignmentHistory history = assignmentHistoryMapping.toEntity(dto);
        history.setEquipment(equipment);
        history.setEmployee(employee);
        history.setAssignedAt(LocalDateTime.now());
        assignmentHistoryRepository.save(history);

        log.info("Запись о выдаче создана. ID записи: {}", history.getId());
        log.info("=== УСПЕШНО: Оборудование выдано ===");

        return assignmentHistoryMapping.toDto(history);
    }

    @Transactional
    public AssignmentHistoryDto returnEquipment(ReturnEquipmentDto dto) {
        log.info("=== НАЧАЛО: Возврат оборудования ===");
        log.info("ID записи: {}", dto.getId());

        AssignmentHistory history = assignmentHistoryRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Запись о выдаче с ID {} не найдена", dto.getId());
                    return new EntityNotFoundException(
                            String.format("Assignment record with id '%d' not found", dto.getId())
                    );
                });

        if (history.getReturnedAt() != null) {
            log.warn("Оборудование уже возвращено ранее. Дата возврата: {}", history.getReturnedAt());
            throw new IllegalArgumentException("Equipment already returned");
        }

        history.setReturnedAt(LocalDateTime.now());
        if (dto.getCondition() != null) {
            history.setCondition(dto.getCondition());
            log.info("Состояние оборудования: {}", dto.getCondition());
        }
        assignmentHistoryRepository.save(history);
        log.info("Дата возврата установлена: {}", history.getReturnedAt());

        boolean hasActiveAssignments = assignmentHistoryRepository
                .findByEquipmentIdAndReturnedAtIsNull(history.getEquipment().getId())
                .isPresent();

        if (!hasActiveAssignments) {
            history.getEquipment().setStatus(EquipmentStatus.IN_STOCK);
            equipmentRepository.save(history.getEquipment());
            log.info("Статус оборудования изменён на IN_STOCK (нет активных выдач)");
        } else {
            log.info("Статус оборудования остаётся ASSIGNED (есть другие активные выдачи)");
        }

        log.info("=== УСПЕШНО: Оборудование возвращено ===");
        return assignmentHistoryMapping.toDto(history);
    }

    public Page<AssignmentHistoryDto> getAllAssignments(Pageable pageable) {
        log.debug("Запрос всех записей истории с пагинацией");
        Page<AssignmentHistory> histories = assignmentHistoryRepository.findAll(pageable);
        if (histories.isEmpty()) {
            log.warn("Записи истории не найдены");
            throw new NoEntitiesFoundException("No assignment records found");
        }
        log.debug("Найдено {} записей", histories.getTotalElements());
        return histories.map(assignmentHistoryMapping::toDto);
    }

    public Page<AssignmentHistoryDto> getAssignmentsByEquipmentId(Long equipmentId, Pageable pageable) {
        log.debug("Поиск истории по оборудованию ID: {}", equipmentId);

        if (!equipmentRepository.existsById(equipmentId)) {
            log.error("Оборудование с ID {} не найдено", equipmentId);
            throw new EntityNotFoundException(
                    String.format("Equipment with id '%d' not found", equipmentId)
            );
        }

        Page<AssignmentHistory> histories = assignmentHistoryRepository.findByEquipmentId(equipmentId, pageable);
        if (histories.isEmpty()) {
            log.warn("История для оборудования ID {} не найдена", equipmentId);
            throw new NoEntitiesFoundException("No assignment records found for this equipment");
        }
        log.debug("Найдено {} записей для оборудования ID {}", histories.getTotalElements(), equipmentId);
        return histories.map(assignmentHistoryMapping::toDto);
    }

    public Page<AssignmentHistoryDto> getAssignmentsByEmployeeId(Long employeeId, Pageable pageable) {
        log.debug("Поиск истории по сотруднику ID: {}", employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            log.error("Сотрудник с ID {} не найден", employeeId);
            throw new EntityNotFoundException(
                    String.format("Employee with id '%d' not found", employeeId)
            );
        }

        Page<AssignmentHistory> histories = assignmentHistoryRepository.findByEmployeeId(employeeId, pageable);
        if (histories.isEmpty()) {
            log.warn("История для сотрудника ID {} не найдена", employeeId);
            throw new NoEntitiesFoundException("No assignment records found for this employee");
        }
        log.debug("Найдено {} записей для сотрудника ID {}", histories.getTotalElements(), employeeId);
        return histories.map(assignmentHistoryMapping::toDto);
    }

    public AssignmentHistoryDto getAssignmentById(Long id) {
        log.debug("Поиск записи истории по ID: {}", id);

        AssignmentHistory history = assignmentHistoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Запись истории с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Assignment record with id '%d' not found", id)
                    );
                });
        return assignmentHistoryMapping.toDto(history);
    }

    public AssignmentHistoryDto getCurrentAssignmentByEquipmentId(Long equipmentId) {
        log.debug("Поиск активной выдачи по оборудованию ID: {}", equipmentId);

        AssignmentHistory history = assignmentHistoryRepository.findByEquipmentIdAndReturnedAtIsNull(equipmentId)
                .orElseThrow(() -> {
                    log.warn("Активная выдача для оборудования ID {} не найдена", equipmentId);
                    return new EntityNotFoundException(
                            String.format("No active assignment found for equipment with id '%d'", equipmentId)
                    );
                });
        return assignmentHistoryMapping.toDto(history);
    }

    public long getCountAssignments() {
        log.debug("Подсчёт общего количества задач");

        return assignmentHistoryRepository.count();
    }

    public long getCountAssignmentsWhereReturnedAtIsNull() {
        log.debug("Подсчёт количества активных задач");

        return assignmentHistoryRepository.countByReturnedAtIsNull();
    }
}