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
            description = "Создаёт новую материнскую плату с указанными параметрами. Доступно только для администраторов. Возвращает созданный объект MotherboardDto.",
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
            description = "Возвращает страницу со списком всех материнских плат с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllMotherboards(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<MotherboardDto> motherboards = motherboardService.getAllMotherboards(pageable);
        return ResponseEntity.ok(motherboards);
    }

    @Operation(
            summary = "Поиск материнской платы по id",
            description = "Возвращает материнскую плату по её идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getMotherboardById(@PathVariable Long id) {
        MotherboardDto motherboard = motherboardService.getMotherboardById(id);
        return ResponseEntity.ok(motherboard);
    }

    @Operation(
            summary = "Поиск материнской платы по названию",
            description = "Возвращает материнскую плату по её названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getMotherboardByName(@PathVariable String name) {
        MotherboardDto motherboard = motherboardService.getMotherboardByName(name);
        return ResponseEntity.ok(motherboard);
    }

    @Operation(
            summary = "Обновление материнской платы (ADMIN)",
            description = "Обновляет данные существующей материнской платы по её id. Доступно только для администраторов. Возвращает обновлённый объект MotherboardDto.",
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
            summary = "Удаление материнской платы (ADMIN)",
            description = "Удаляет материнскую плату по её id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMotherboard(@PathVariable Long id) {
        motherboardService.deleteMotherboard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}