package ru.litvast.techtrackapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.model.entity.equipment.EquipmentStatus;
import ru.litvast.techtrackapi.service.EquipmentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment")
@Tag(name = "equipments", description = "Методы для работы с оборудованием")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(
            summary = "Подсчёт количества оборудования по статусу",
            description = "Возвращает общее количество единиц оборудования, имеющих указанный статус. Статус передаётся в пути запроса и должен соответствовать значению из перечисления EquipmentStatus. Требует наличия валидного access-токена.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count/by-status/{status}")
    public ResponseEntity<?> getCountEquipmentsByStatus(@PathVariable EquipmentStatus status) {
        long countEquipments = equipmentService.countEquipmentsByStatus(status);
        return ResponseEntity.ok(countEquipments);
    }
}