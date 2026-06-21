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
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerDto;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerUpdateDto;
import ru.litvast.techtrackapi.service.ComputerService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/computer")
@Tag(name = "computers", description = "Методы для работы с компьютерами")
public class ComputerController {

    private final ComputerService computerService;

    @Operation(
            summary = "Добавление компьютера (ADMIN)",
            description = "Создаёт новый компьютер с указанными параметрами и комплектующими. Доступно только для администраторов. Возвращает созданный объект ComputerDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addComputer(@Valid @RequestBody ComputerDto computerDto) {
        ComputerDto created = computerService.addComputer(computerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех компьютеров с пагинацией",
            description = "Возвращает страницу со списком всех компьютеров с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllComputers(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ComputerDto> computers = computerService.getAllComputers(pageable);
        return ResponseEntity.ok(computers);
    }

    @Operation(
            summary = "Поиск компьютера по id",
            description = "Возвращает компьютер по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getComputerById(@PathVariable Long id) {
        ComputerDto computer = computerService.getComputerById(id);
        return ResponseEntity.ok(computer);
    }

    @Operation(
            summary = "Поиск компьютера по названию",
            description = "Возвращает компьютер по его названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getComputerByName(@PathVariable String name) {
        ComputerDto computer = computerService.getComputerByName(name);
        return ResponseEntity.ok(computer);
    }

    @Operation(
            summary = "Подсчёт общего количества компьютеров",
            description = "Возвращает общее количество компьютеров в системе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountComputers() {
        long countComputers = computerService.getCountComputers();
        return ResponseEntity.ok(countComputers);
    }

    @Operation(
            summary = "Обновление компьютера по id (ADMIN)",
            description = "Обновляет данные существующего компьютера по его id. Доступно только для администраторов. Возвращает обновлённый объект ComputerDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateComputer(@PathVariable Long id,
                                            @Valid @RequestBody ComputerUpdateDto computerDto) {
        computerDto.setId(id);
        ComputerDto updated = computerService.updateComputer(computerDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление компьютера по id (ADMIN)",
            description = "Удаляет компьютер по его id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComputer(@PathVariable Long id) {
        computerService.deleteComputer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}