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
            summary = "Добавление одной планки RAM (ADMIN)",
            description = "Создаёт новую запись оперативной памяти с указанными характеристиками. Доступно только для администраторов. Возвращает созданный объект RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRam(@Valid @RequestBody RamDto ramDto) {
        RamDto created = ramService.addRam(ramDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Добавление нескольких планок RAM (ADMIN)",
            description = "Создаёт несколько записей оперативной памяти за один запрос. Принимает список DTO. Доступно только для администраторов. Возвращает список созданных объектов RamDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSomeRam(@Valid @RequestBody List<RamDto> ramDtoList) {
        List<RamDto> created = ramService.addSomeRam(ramDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех планок RAM с пагинацией",
            description = "Возвращает страницу со списком всех записей оперативной памяти с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllRams(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<RamDto> rams = ramService.getAllRams(pageable);
        return ResponseEntity.ok(rams);
    }

    @Operation(
            summary = "Поиск планки RAM по id",
            description = "Возвращает запись оперативной памяти по её идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRamById(@PathVariable Long id) {
        RamDto ram = ramService.getRamById(id);
        return ResponseEntity.ok(ram);
    }

    @Operation(
            summary = "Поиск планки RAM по названию",
            description = "Возвращает запись оперативной памяти по её названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getRamByName(@PathVariable String name) {
        RamDto ram = ramService.getRamByName(name);
        return ResponseEntity.ok(ram);
    }

    @Operation(
            summary = "Обновление планки RAM (ADMIN)",
            description = "Обновляет данные существующей записи оперативной памяти по её id. Доступно только для администраторов. Возвращает обновлённый объект RamDto.",
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
            summary = "Удаление планки RAM (ADMIN)",
            description = "Удаляет запись оперативной памяти по её id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRam(@PathVariable Long id) {
        ramService.deleteRam(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}