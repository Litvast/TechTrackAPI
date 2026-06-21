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
import ru.litvast.techtrackapi.model.dto.RoomDto;
import ru.litvast.techtrackapi.model.dto.RoomUpdateDto;
import ru.litvast.techtrackapi.service.RoomService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rooms")
@Tag(name = "rooms", description = "Методы для работы с комнатами")
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "Добавление комнаты (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRoom(@Valid @RequestBody RoomDto dto) {
        RoomDto created = roomService.addRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех комнат с пагинацией",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllRooms(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<RoomDto> rooms = roomService.getAllRooms(pageable);
        return ResponseEntity.ok(rooms);
    }

    @Operation(
            summary = "Получение комнат по этажу",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-floor/{buildingFloorId}")
    public ResponseEntity<?> getRoomsByBuildingFloorId(@PathVariable Long buildingFloorId,
                                                       @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<RoomDto> rooms = roomService.getRoomsByBuildingFloorId(buildingFloorId, pageable);
        return ResponseEntity.ok(rooms);
    }

    @Operation(
            summary = "Поиск комнаты по id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id) {
        RoomDto room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @Operation(
            summary = "Поиск комнаты по названию",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getRoomByName(@PathVariable String name) {
        RoomDto room = roomService.getRoomByName(name);
        return ResponseEntity.ok(room);
    }

    @Operation(
            summary = "Подсчёт общего количества комнат",
            description = "В ответ выдаётся подсчитанное количество комнат",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountRooms() {
        long count = roomService.getCountRooms();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Обновление комнаты (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRoom(@PathVariable Long id,
                                        @Valid @RequestBody RoomUpdateDto dto) {
        RoomDto updated = roomService.updateRoom(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление комнаты (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Successfully");
    }
}