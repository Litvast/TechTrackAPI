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
import ru.litvast.techtrackapi.model.dto.equipment.computer.RamDto;
import ru.litvast.techtrackapi.service.RamService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/ram")
@Tag(name = "rams", description = "Методы для работы с оперативной памятью")
public class RamController {

    private final RamService ramService;

    @Operation(
            summary = "Добавление RAM (ADMIN)",
            description = "В ответ выдаётся созданный объект RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRam(@Valid @RequestBody RamDto ramDto) {
        RamDto created = ramService.addRam(ramDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Добавление нескольких RAM (ADMIN)",
            description = "В ответ выдаётся список созданных объектов RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSomeRam(@Valid @RequestBody List<RamDto> ramDtoList) {
        List<RamDto> created = ramService.addSomeRam(ramDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех RAM с пагинацией",
            description = "В ответ выдаётся страница с объектами RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllRams(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<RamDto> rams = ramService.getAllRams(pageable);
        return ResponseEntity.ok(rams);
    }

    @Operation(
            summary = "Поиск RAM по id",
            description = "В ответ выдаётся найденный объект RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRamById(@PathVariable Long id) {
        RamDto ram = ramService.getRamById(id);
        return ResponseEntity.ok(ram);
    }

    @Operation(
            summary = "Поиск RAM по имени",
            description = "В ответ выдаётся найденный объект RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getRamByName(@PathVariable String name) {
        RamDto ram = ramService.getRamByName(name);
        return ResponseEntity.ok(ram);
    }

    @Operation(
            summary = "Обновление RAM по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRam(@PathVariable Long id,
                                       @Valid @RequestBody RamDto ramDto) {
        RamDto updated = ramService.updateRam(id, ramDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление RAM по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRam(@PathVariable Long id) {
        ramService.deleteRam(id);
        return ResponseEntity.ok("Successfully");
    }
}