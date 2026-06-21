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
import ru.litvast.techtrackapi.model.dto.equipment.computer.MotherboardFormFactorDto;
import ru.litvast.techtrackapi.service.MotherboardFormFactorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/computer/form-factor")
@Tag(name = "motherboard-form-factors", description = "Методы для работы с форм-факторами материнских плат")
public class MotherboardFormFactorController {

    private final MotherboardFormFactorService formFactorService;

    @Operation(
            summary = "Получение всех форм-факторов с пагинацией",
            description = "В ответ выдаётся страница с объектами MotherboardFormFactorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllFormFactors(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<MotherboardFormFactorDto> formFactors = formFactorService.getAllFormFactors(pageable);
        return ResponseEntity.ok(formFactors);
    }

    @Operation(
            summary = "Добавление нового форм-фактора (ADMIN)",
            description = "В ответ выдаётся созданный объект MotherboardFormFactorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addFormFactor(@Valid @RequestBody MotherboardFormFactorDto formFactorDto) {
        MotherboardFormFactorDto created = formFactorService.addFormFactor(formFactorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Обновление форм-фактора по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект MotherboardFormFactorDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFormFactor(@PathVariable Long id, @Valid @RequestBody MotherboardFormFactorDto formFactorDto) {
        MotherboardFormFactorDto updated = formFactorService.updateFormFactor(id, formFactorDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление форм-фактора по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFormFactor(@PathVariable Long id) {
        formFactorService.deleteFormFactor(id);
        return ResponseEntity.ok("Successfully");
    }
}