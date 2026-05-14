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
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
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
            description = "В ответ выдаётся созданный объект ComputerDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addComputer(@Valid @RequestBody ComputerDto computerDto) {
        try {
            ComputerDto created = computerService.addComputer(computerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Computer creation failed");
        }
    }

    @Operation(
            summary = "Получение всех компьютеров с пагинацией",
            description = "В ответ выдаётся страница с объектами ComputerDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllComputers(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        try {
            Page<ComputerDto> computers = computerService.getAllComputers(pageable);
            return ResponseEntity.ok(computers);
        } catch (NoEntitiesFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch computers");
        }
    }

    @Operation(
            summary = "Поиск компьютера по id",
            description = "В ответ выдаётся найденный объект ComputerDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getComputerById(@PathVariable Long id) {
        try {
            ComputerDto computer = computerService.getComputerById(id);
            return ResponseEntity.ok(computer);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to fetch computer");
        }
    }

    @Operation(
            summary = "Обновление компьютера по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект ComputerDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateComputer(@PathVariable Long id,
                                            @Valid @RequestBody ComputerUpdateDto computerDto) {
        try {
            computerDto.setId(id);
            ComputerDto updated = computerService.updateComputer(computerDto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Computer update failed");
        }
    }

    @Operation(
            summary = "Удаление компьютера по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComputer(@PathVariable Long id) {
        try {
            computerService.deleteComputer(id);
            return ResponseEntity.ok("Successfully");
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Computer deletion failed");
        }
    }
}