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
import ru.litvast.techtrackapi.model.dto.BuildingFloorDto;
import ru.litvast.techtrackapi.model.dto.BuildingFloorUpdateDto;
import ru.litvast.techtrackapi.service.BuildingFloorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/building-floors")
@Tag(name = "building-floors", description = "Методы для работы с этажами зданий")
public class BuildingFloorController {

    private final BuildingFloorService buildingFloorService;

    @Operation(
            summary = "Добавление этажа (ADMIN)",
            description = "Создаёт новый этаж в указанном здании. Возвращает созданный объект BuildingFloorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBuildingFloor(@Valid @RequestBody BuildingFloorDto dto) {
        BuildingFloorDto created = buildingFloorService.addBuildingFloor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех этажей с пагинацией",
            description = "Возвращает страницу со списком всех этажей с поддержкой пагинации и сортировки по номеру этажа.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllBuildingFloors(@PageableDefault(size = 20, sort = "floorNumber") Pageable pageable) {
        Page<BuildingFloorDto> floors = buildingFloorService.getAllBuildingFloors(pageable);
        return ResponseEntity.ok(floors);
    }

    @Operation(
            summary = "Получение этажей по зданию",
            description = "Возвращает страницу этажей, принадлежащих указанному зданию. Поддерживает пагинацию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-building/{buildingId}")
    public ResponseEntity<?> getFloorsByBuildingId(@PathVariable Long buildingId,
                                                   @PageableDefault(size = 20, sort = "floorNumber") Pageable pageable) {
        Page<BuildingFloorDto> floors = buildingFloorService.getFloorsByBuildingId(buildingId, pageable);
        return ResponseEntity.ok(floors);
    }

    @Operation(
            summary = "Поиск этажа по id",
            description = "Возвращает этаж по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getBuildingFloorById(@PathVariable Long id) {
        BuildingFloorDto floor = buildingFloorService.getBuildingFloorById(id);
        return ResponseEntity.ok(floor);
    }

    @Operation(
            summary = "Поиск этажа по номеру и зданию",
            description = "Возвращает этаж по его номеру и идентификатору здания (уникальная пара).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-number/{floorNumber}/building/{buildingId}")
    public ResponseEntity<?> getBuildingFloorByNumberAndBuilding(@PathVariable Integer floorNumber,
                                                                 @PathVariable Long buildingId) {
        BuildingFloorDto floor = buildingFloorService.getBuildingFloorByNumberAndBuilding(floorNumber, buildingId);
        return ResponseEntity.ok(floor);
    }

    @Operation(
            summary = "Подсчёт общего количества этажей",
            description = "Возвращает общее количество этажей в системе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountFloors() {
        long count = buildingFloorService.getCountFloors();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Обновление этажа (ADMIN)",
            description = "Обновляет данные существующего этажа по его id. Возвращает обновлённый объект BuildingFloorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBuildingFloor(@PathVariable Long id,
                                                 @Valid @RequestBody BuildingFloorUpdateDto dto) {
        BuildingFloorDto updated = buildingFloorService.updateBuildingFloor(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление этажа (ADMIN)",
            description = "Удаляет этаж по его id. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBuildingFloor(@PathVariable Long id) {
        buildingFloorService.deleteBuildingFloor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}