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
            description = "Создаёт новый блок питания с указанными параметрами. Доступно только для администраторов. Возвращает созданный объект PowerSupplyDto.",
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
            description = "Возвращает страницу со списком всех блоков питания с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllPowerSupplies(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<PowerSupplyDto> powerSupplies = powerSupplyService.getAllPowerSupplies(pageable);
        return ResponseEntity.ok(powerSupplies);
    }

    @Operation(
            summary = "Поиск блока питания по id",
            description = "Возвращает блок питания по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getPowerSupplyById(@PathVariable Long id) {
        PowerSupplyDto powerSupply = powerSupplyService.getPowerSupplyById(id);
        return ResponseEntity.ok(powerSupply);
    }

    @Operation(
            summary = "Поиск блока питания по названию",
            description = "Возвращает блок питания по его названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getPowerSupplyByName(@PathVariable String name) {
        PowerSupplyDto powerSupply = powerSupplyService.getPowerSupplyByName(name);
        return ResponseEntity.ok(powerSupply);
    }

    @Operation(
            summary = "Обновление блока питания (ADMIN)",
            description = "Обновляет данные существующего блока питания по его id. Доступно только для администраторов. Возвращает обновлённый объект PowerSupplyDto.",
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
            summary = "Удаление блока питания (ADMIN)",
            description = "Удаляет блок питания по его id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePowerSupply(@PathVariable Long id) {
        powerSupplyService.deletePowerSupply(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}