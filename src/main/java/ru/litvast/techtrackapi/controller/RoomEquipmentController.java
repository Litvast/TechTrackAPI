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
import ru.litvast.techtrackapi.model.dto.RoomEquipmentDto;
import ru.litvast.techtrackapi.model.dto.RoomEquipmentUpdateDto;
import ru.litvast.techtrackapi.service.RoomEquipmentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/room-equipment")
@Tag(name = "room-equipment", description = "Методы для работы с оборудованием в комнатах")
public class RoomEquipmentController {

    private final RoomEquipmentService roomEquipmentService;

    @Operation(
            summary = "Добавление оборудования в комнату (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRoomEquipment(@Valid @RequestBody RoomEquipmentDto dto) {
        RoomEquipmentDto created = roomEquipmentService.addRoomEquipment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех записей оборудования в комнатах с пагинацией",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllRoomEquipments(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<RoomEquipmentDto> items = roomEquipmentService.getAllRoomEquipments(pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(
            summary = "Получение оборудования по комнате",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-room/{roomId}")
    public ResponseEntity<?> getRoomEquipmentsByRoomId(@PathVariable Long roomId,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        Page<RoomEquipmentDto> items = roomEquipmentService.getRoomEquipmentsByRoomId(roomId, pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(
            summary = "Получение комнат по оборудованию",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-equipment/{equipmentId}")
    public ResponseEntity<?> getRoomEquipmentsByEquipmentId(@PathVariable Long equipmentId,
                                                            @PageableDefault(size = 20) Pageable pageable) {
        Page<RoomEquipmentDto> items = roomEquipmentService.getRoomEquipmentsByEquipmentId(equipmentId, pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(
            summary = "Поиск записи по id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomEquipmentById(@PathVariable Long id) {
        RoomEquipmentDto item = roomEquipmentService.getRoomEquipmentById(id);
        return ResponseEntity.ok(item);
    }

    @Operation(
            summary = "Подсчёт общего количества записей об оборудовании в комнатах",
            description = "В ответ выдаётся подсчитанное количество записей",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountRoomEquipments() {
        long count = roomEquipmentService.getCountRoomEquipments();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Обновление записи (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoomEquipment(@PathVariable Long id,
                                                 @Valid @RequestBody RoomEquipmentUpdateDto dto) {
        RoomEquipmentDto updated = roomEquipmentService.updateRoomEquipment(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление записи (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoomEquipment(@PathVariable Long id) {
        roomEquipmentService.deleteRoomEquipment(id);
        return ResponseEntity.ok("Successfully");
    }
}