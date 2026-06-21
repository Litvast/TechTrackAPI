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
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuArchitectureDto;
import ru.litvast.techtrackapi.service.CpuArchitectureService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/computer/cpu-architecture")
@Tag(name = "cpu-architectures", description = "Методы для работы с архитектурами процессоров")
public class CpuArchitectureController {

    private final CpuArchitectureService cpuArchitectureService;

    @Operation(
            summary = "Добавление новой архитектуры (ADMIN)",
            description = "Создаёт новую архитектуру процессора. Доступно только для администраторов. Возвращает созданный объект CpuArchitectureDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addCpuArchitecture(@Valid @RequestBody CpuArchitectureDto cpuArchitectureDto) {
        CpuArchitectureDto created = cpuArchitectureService.addCpuArchitecture(cpuArchitectureDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех архитектур процессоров с пагинацией",
            description = "Возвращает страницу со списком всех архитектур процессоров с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllCpuArchitectures(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<CpuArchitectureDto> cpuArchitectures = cpuArchitectureService.getAllCpuArchitectures(pageable);
        return ResponseEntity.ok(cpuArchitectures);
    }

    @Operation(
            summary = "Обновление архитектуры по id (ADMIN)",
            description = "Обновляет данные существующей архитектуры процессора по её id. Доступно только для администраторов. Возвращает обновлённый объект CpuArchitectureDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCpuArchitecture(@PathVariable Long id, @Valid @RequestBody CpuArchitectureDto cpuArchitectureDto) {
        CpuArchitectureDto updated = cpuArchitectureService.updateCpuArchitecture(id, cpuArchitectureDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление архитектуры по id (ADMIN)",
            description = "Удаляет архитектуру процессора по её id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCpuArchitecture(@PathVariable Long id) {
        cpuArchitectureService.deleteCpuArchitecture(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}