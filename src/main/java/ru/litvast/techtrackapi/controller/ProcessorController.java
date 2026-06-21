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
import ru.litvast.techtrackapi.model.dto.equipment.computer.ProcessorDto;
import ru.litvast.techtrackapi.service.ProcessorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/processor")
@Tag(name = "processors", description = "Методы для работы с процессорами")
public class ProcessorController {

    private final ProcessorService processorService;

    @Operation(
            summary = "Добавление процессора (ADMIN)",
            description = "Создаёт новый процессор с указанными характеристиками. Доступно только для администраторов. Возвращает созданный объект ProcessorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProcessor(@Valid @RequestBody ProcessorDto processorDto) {
        ProcessorDto created = processorService.addProcessor(processorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех процессоров с пагинацией",
            description = "Возвращает страницу со списком всех процессоров с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllProcessors(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProcessorDto> processors = processorService.getAllProcessors(pageable);
        return ResponseEntity.ok(processors);
    }

    @Operation(
            summary = "Поиск процессора по id",
            description = "Возвращает процессор по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getProcessorById(@PathVariable Long id) {
        ProcessorDto processor = processorService.getProcessorById(id);
        return ResponseEntity.ok(processor);
    }

    @Operation(
            summary = "Поиск процессора по названию",
            description = "Возвращает процессор по его названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getProcessorByName(@PathVariable String name) {
        ProcessorDto processor = processorService.getProcessorByName(name);
        return ResponseEntity.ok(processor);
    }

    @Operation(
            summary = "Обновление процессора (ADMIN)",
            description = "Обновляет данные существующего процессора по его id. Доступно только для администраторов. Возвращает обновлённый объект ProcessorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProcessor(@PathVariable Long id,
                                             @Valid @RequestBody ProcessorDto processorDto) {
        ProcessorDto updated = processorService.updateProcessor(id, processorDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление процессора (ADMIN)",
            description = "Удаляет процессор по его id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProcessor(@PathVariable Long id) {
        processorService.deleteProcessor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}