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
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuSocketDto;
import ru.litvast.techtrackapi.service.CpuSocketService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/computer/cpu-socket")
@Tag(name = "cpu-sockets", description = "Методы для работы с сокетами")
public class CpuSocketController {

    private final CpuSocketService cpuSocketService;

    @Operation(
            summary = "Получение всех сокетов с пагинацией",
            description = "В ответ выдаётся страница с объектами CpuSocketDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllCpuSockets(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<CpuSocketDto> cpuSockets = cpuSocketService.getAllCpuSockets(pageable);
        return ResponseEntity.ok(cpuSockets);
    }

    @Operation(
            summary = "Добавление нового сокета (ADMIN)",
            description = "В ответ выдаётся созданный объект CpuSocketDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addCpuSocket(@Valid @RequestBody CpuSocketDto cpuSocketDto) {
        CpuSocketDto created = cpuSocketService.addCpuSocket(cpuSocketDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Обновление сокета по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект CpuSocketDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCpuSocket(@PathVariable Long id, @Valid @RequestBody CpuSocketDto cpuSocketDto) {
        CpuSocketDto updated = cpuSocketService.updateCpuSocket(id, cpuSocketDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление сокета по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCpuSocket(@PathVariable Long id) {
        cpuSocketService.deleteCpuSocket(id);
        return ResponseEntity.ok("Successfully");
    }
}