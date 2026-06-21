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
            description = "Создаёт новую запись о привязке оборудования к комнате. Доступно только для администраторов. Возвращает созданный объект RoomEquipmentDto.",
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
            description = "Возвращает страницу со всеми записями о привязке оборудования к комнатам с поддержкой пагинации и сортировки по id.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllRoomEquipments(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<RoomEquipmentDto> items = roomEquipmentService.getAllRoomEquipments(pageable);
        return ResponseEntity.ok(items);
    }

    @Operation(
            summary = "Получение оборудования по комнате",
            description = "Возвращает страницу записей о привязке оборудования к указанной комнате. Поддерживает пагинацию.",
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
            description = "Возвращает страницу записей о привязке указанного оборудования к комнатам. Поддерживает пагинацию.",
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
            description = "Возвращает запись о привязке оборудования к комнате по её идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomEquipmentById(@PathVariable Long id) {
        RoomEquipmentDto item = roomEquipmentService.getRoomEquipmentById(id);
        return ResponseEntity.ok(item);
    }

    @Operation(
            summary = "Подсчёт общего количества записей об оборудовании в комнатах",
            description = "Возвращает общее количество записей о привязке оборудования к комнатам в системе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountRoomEquipments() {
        long count = roomEquipmentService.getCountRoomEquipments();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Обновление записи (ADMIN)",
            description = "Обновляет данные существующей записи о привязке оборудования к комнате. Доступно только для администраторов. Возвращает обновлённый объект RoomEquipmentDto.",
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
            description = "Удаляет запись о привязке оборудования к комнате по её id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoomEquipment(@PathVariable Long id) {
        roomEquipmentService.deleteRoomEquipment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}