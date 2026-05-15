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
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllBuildingFloors(@PageableDefault(size = 20, sort = "floorNumber") Pageable pageable) {
        Page<BuildingFloorDto> floors = buildingFloorService.getAllBuildingFloors(pageable);
        return ResponseEntity.ok(floors);
    }

    @Operation(
            summary = "Получение этажей по зданию",
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
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getBuildingFloorById(@PathVariable Long id) {
        BuildingFloorDto floor = buildingFloorService.getBuildingFloorById(id);
        return ResponseEntity.ok(floor);
    }

    @Operation(
            summary = "Поиск этажа по номеру и зданию",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-number/{floorNumber}/building/{buildingId}")
    public ResponseEntity<?> getBuildingFloorByNumberAndBuilding(@PathVariable Integer floorNumber,
                                                                 @PathVariable Long buildingId) {
        BuildingFloorDto floor = buildingFloorService.getBuildingFloorByNumberAndBuilding(floorNumber, buildingId);
        return ResponseEntity.ok(floor);
    }

    @Operation(
            summary = "Обновление этажа (ADMIN)",
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
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBuildingFloor(@PathVariable Long id) {
        buildingFloorService.deleteBuildingFloor(id);
        return ResponseEntity.ok("Successfully");
    }
}