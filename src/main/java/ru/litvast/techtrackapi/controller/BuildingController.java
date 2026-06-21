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
import ru.litvast.techtrackapi.model.dto.BuildingDto;
import ru.litvast.techtrackapi.model.dto.BuildingUpdateDto;
import ru.litvast.techtrackapi.service.BuildingService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/buildings")
@Tag(name = "buildings", description = "Методы для работы со зданиями")
public class BuildingController {

    private final BuildingService buildingService;

    @Operation(
            summary = "Добавление здания (ADMIN)",
            description = "Создаёт новое здание и возвращает его DTO.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBuilding(@Valid @RequestBody BuildingDto dto) {
        BuildingDto created = buildingService.addBuilding(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех зданий с пагинацией",
            description = "Возвращает страницу со списком зданий.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllBuildings(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<BuildingDto> buildings = buildingService.getAllBuildings(pageable);
        return ResponseEntity.ok(buildings);
    }

    @Operation(
            summary = "Получение зданий по компании",
            description = "Возвращает страницу зданий, принадлежащих указанной компании.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<?> getBuildingsByCompanyId(@PathVariable Long companyId,
                                                     @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<BuildingDto> buildings = buildingService.getBuildingsByCompanyId(companyId, pageable);
        return ResponseEntity.ok(buildings);
    }

    @Operation(
            summary = "Поиск здания по id",
            description = "Возвращает здание по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getBuildingById(@PathVariable Long id) {
        BuildingDto building = buildingService.getBuildingById(id);
        return ResponseEntity.ok(building);
    }

    @Operation(
            summary = "Поиск здания по названию",
            description = "Возвращает здание по его названию (регистронезависимо).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getBuildingByName(@PathVariable String name) {
        BuildingDto building = buildingService.getBuildingByName(name);
        return ResponseEntity.ok(building);
    }

    @Operation(
            summary = "Подсчёт общего количества зданий",
            description = "Возвращает общее количество зданий в системе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountBuildings() {
        long count = buildingService.getCountBuildings();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Обновление здания (ADMIN)",
            description = "Обновляет данные существующего здания по его id и возвращает обновлённый DTO.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBuilding(@PathVariable Long id,
                                            @Valid @RequestBody BuildingUpdateDto dto) {
        BuildingDto updated = buildingService.updateBuilding(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление здания (ADMIN)",
            description = "Удаляет здание по его id. Возвращает сообщение об успехе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}