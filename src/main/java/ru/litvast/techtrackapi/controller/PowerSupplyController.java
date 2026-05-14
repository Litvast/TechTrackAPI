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
import ru.litvast.techtrackapi.model.dto.equipment.computer.PowerSupplyDto;
import ru.litvast.techtrackapi.service.PowerSupplyService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/power-supply")
@Tag(name = "power-supplies", description = "Методы для работы с блоками питания")
public class PowerSupplyController {

    private final PowerSupplyService powerSupplyService;

    @Operation(
            summary = "Добавление блока питания (ADMIN)",
            description = "В ответ выдаётся созданный объект PowerSupplyDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPowerSupply(@Valid @RequestBody PowerSupplyDto powerSupplyDto) {
        PowerSupplyDto created = powerSupplyService.addPowerSupply(powerSupplyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех блоков питания с пагинацией",
            description = "В ответ выдаётся страница с объектами PowerSupplyDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllPowerSupplies(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<PowerSupplyDto> powerSupplies = powerSupplyService.getAllPowerSupplies(pageable);
        return ResponseEntity.ok(powerSupplies);
    }

    @Operation(
            summary = "Поиск блока питания по id",
            description = "В ответ выдаётся найденный объект PowerSupplyDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getPowerSupplyById(@PathVariable Long id) {
        PowerSupplyDto powerSupply = powerSupplyService.getPowerSupplyById(id);
        return ResponseEntity.ok(powerSupply);
    }

    @Operation(
            summary = "Поиск блока питания по имени",
            description = "В ответ выдаётся найденный объект PowerSupplyDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getPowerSupplyByName(@PathVariable String name) {
        PowerSupplyDto powerSupply = powerSupplyService.getPowerSupplyByName(name);
        return ResponseEntity.ok(powerSupply);
    }

    @Operation(
            summary = "Обновление блока питания по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект PowerSupplyDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePowerSupply(@PathVariable Long id,
                                               @Valid @RequestBody PowerSupplyDto powerSupplyDto) {
        PowerSupplyDto updated = powerSupplyService.updatePowerSupply(id, powerSupplyDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление блока питания по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePowerSupply(@PathVariable Long id) {
        powerSupplyService.deletePowerSupply(id);
        return ResponseEntity.ok("Successfully");
    }
}