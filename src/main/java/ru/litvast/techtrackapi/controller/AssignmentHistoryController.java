package ru.litvast.techtrackapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.model.dto.AssignmentHistoryDto;
import ru.litvast.techtrackapi.model.dto.CreateAssignmentDto;
import ru.litvast.techtrackapi.model.dto.ReturnEquipmentDto;
import ru.litvast.techtrackapi.service.AssignmentHistoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/assignments")
@Tag(name = "assignments", description = "Методы для работы с историей назначений")
public class AssignmentHistoryController {

    private final AssignmentHistoryService assignmentHistoryService;

    @Operation(
            summary = "Выдать оборудование сотруднику (ADMIN)",
            description = "Создаёт новую запись о выдаче оборудования сотруднику. Автоматически обновляет статус оборудования на ASSIGNED. Возвращает созданный объект AssignmentHistoryDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignEquipment(@Valid @RequestBody CreateAssignmentDto dto) {
        AssignmentHistoryDto created = assignmentHistoryService.assignEquipment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Вернуть оборудование (ADMIN)",
            description = "Фиксирует возврат оборудования от сотрудника. Обновляет дату возврата и состояние. При отсутствии других активных выдач статус оборудования меняется на IN_STOCK. Возвращает обновлённый объект AssignmentHistoryDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> returnEquipment(@Valid @RequestBody ReturnEquipmentDto dto) {
        AssignmentHistoryDto returned = assignmentHistoryService.returnEquipment(dto);
        return ResponseEntity.ok(returned);
    }

    @Operation(
            summary = "Получение всех записей истории с пагинацией",
            description = "Возвращает страницу со всеми записями истории назначений с поддержкой пагинации и сортировки по дате назначения.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllAssignments(@PageableDefault(size = 20, sort = "assignedAt") Pageable pageable) {
        Page<AssignmentHistoryDto> assignments = assignmentHistoryService.getAllAssignments(pageable);
        return ResponseEntity.ok(assignments);
    }

    @Operation(
            summary = "Поиск записей по оборудованию",
            description = "Возвращает страницу записей истории для указанного оборудования. Поддерживает пагинацию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-equipment/{equipmentId}")
    public ResponseEntity<?> getAssignmentsByEquipmentId(@PathVariable Long equipmentId,
                                                         @PageableDefault(size = 20) Pageable pageable) {
        Page<AssignmentHistoryDto> assignments = assignmentHistoryService.getAssignmentsByEquipmentId(equipmentId, pageable);
        return ResponseEntity.ok(assignments);
    }

    @Operation(
            summary = "Поиск записей по сотруднику",
            description = "Возвращает страницу записей истории для указанного сотрудника. Поддерживает пагинацию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<?> getAssignmentsByEmployeeId(@PathVariable Long employeeId,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        Page<AssignmentHistoryDto> assignments = assignmentHistoryService.getAssignmentsByEmployeeId(employeeId, pageable);
        return ResponseEntity.ok(assignments);
    }

    @Operation(
            summary = "Поиск записи по id",
            description = "Возвращает запись истории назначений по её идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable Long id) {
        AssignmentHistoryDto assignment = assignmentHistoryService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    @Operation(
            summary = "Получение текущего назначения оборудования",
            description = "Возвращает активную (незакрытую) запись о выдаче для указанного оборудования (где returnedAt == null). Если активной записи нет, возвращает пустой объект или соответствующий статус.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/current/by-equipment/{equipmentId}")
    public ResponseEntity<?> getCurrentAssignmentByEquipmentId(@PathVariable Long equipmentId) {
        AssignmentHistoryDto assignment = assignmentHistoryService.getCurrentAssignmentByEquipmentId(equipmentId);
        return ResponseEntity.ok(assignment);
    }

    @Operation(
            summary = "Подсчёт общего количества задач",
            description = "Возвращает общее количество записей в истории назначений.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountAssignments() {
        long countAssignments = assignmentHistoryService.getCountAssignments();
        return ResponseEntity.ok(countAssignments);
    }

    @Operation(
            summary = "Подсчёт количества активных задач",
            description = "Возвращает количество активных (незакрытых) назначений, у которых поле returnedAt == null.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count/active")
    public ResponseEntity<?> getCountActiveAssignments() {
        long countActiveAssignments = assignmentHistoryService.getCountAssignmentsWhereReturnedAtIsNull();
        return ResponseEntity.ok(countActiveAssignments);
    }
}