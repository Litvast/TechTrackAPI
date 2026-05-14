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
import ru.litvast.techtrackapi.model.dto.equipment.computer.MotherboardDto;
import ru.litvast.techtrackapi.service.MotherboardService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/motherboard")
@Tag(name = "motherboards", description = "Методы для работы с материнскими платами")
public class MotherboardController {

    private final MotherboardService motherboardService;

    @Operation(
            summary = "Добавление материнской платы (ADMIN)",
            description = "В ответ выдаётся созданный объект MotherboardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMotherboard(@Valid @RequestBody MotherboardDto motherboardDto) {
        MotherboardDto created = motherboardService.addMotherboard(motherboardDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех материнских плат с пагинацией",
            description = "В ответ выдаётся страница с объектами MotherboardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllMotherboards(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<MotherboardDto> motherboards = motherboardService.getAllMotherboards(pageable);
        return ResponseEntity.ok(motherboards);
    }

    @Operation(
            summary = "Поиск материнской платы по id",
            description = "В ответ выдаётся найденный объект MotherboardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getMotherboardById(@PathVariable Long id) {
        MotherboardDto motherboard = motherboardService.getMotherboardById(id);
        return ResponseEntity.ok(motherboard);
    }

    @Operation(
            summary = "Поиск материнской платы по имени",
            description = "В ответ выдаётся найденный объект MotherboardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getMotherboardByName(@PathVariable String name) {
        MotherboardDto motherboard = motherboardService.getMotherboardByName(name);
        return ResponseEntity.ok(motherboard);
    }

    @Operation(
            summary = "Обновление материнской платы по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект MotherboardDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMotherboard(@PathVariable Long id,
                                               @Valid @RequestBody MotherboardDto motherboardDto) {
        MotherboardDto updated = motherboardService.updateMotherboard(id, motherboardDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление материнской платы по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMotherboard(@PathVariable Long id) {
        motherboardService.deleteMotherboard(id);
        return ResponseEntity.ok("Successfully");
    }
}