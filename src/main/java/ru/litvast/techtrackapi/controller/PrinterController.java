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
import ru.litvast.techtrackapi.model.dto.equipment.PrinterDto;
import ru.litvast.techtrackapi.model.dto.equipment.PrinterUpdateDto;
import ru.litvast.techtrackapi.service.PrinterService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/printer")
@Tag(name = "printers", description = "Методы для работы с принтерами")
public class PrinterController {

    private final PrinterService printerService;

    @Operation(
            summary = "Добавление принтера (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPrinter(@Valid @RequestBody PrinterDto dto) {
        PrinterDto created = printerService.addPrinter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех принтеров с пагинацией",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllPrinters(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<PrinterDto> printers = printerService.getAllPrinters(pageable);
        return ResponseEntity.ok(printers);
    }

    @Operation(
            summary = "Поиск принтера по id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrinterById(@PathVariable Long id) {
        PrinterDto printer = printerService.getPrinterById(id);
        return ResponseEntity.ok(printer);
    }

    @Operation(
            summary = "Поиск принтера по имени",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getPrinterByName(@PathVariable String name) {
        PrinterDto printer = printerService.getPrinterByName(name);
        return ResponseEntity.ok(printer);
    }

    @Operation(
            summary = "Поиск принтера по инвентарному номеру",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-inventory/{inventoryNumber}")
    public ResponseEntity<?> getPrinterByInventoryNumber(@PathVariable String inventoryNumber) {
        PrinterDto printer = printerService.getPrinterByInventoryNumber(inventoryNumber);
        return ResponseEntity.ok(printer);
    }

    @Operation(
            summary = "Подсчёт общего количества принтеров",
            description = "В ответ выдаётся подсчитанное количество принтеров.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountPrinters() {
        long countPrinters = printerService.getCountPrinters();
        return ResponseEntity.ok(countPrinters);
    }

    @Operation(
            summary = "Обновление принтера (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePrinter(@PathVariable Long id,
                                           @Valid @RequestBody PrinterUpdateDto dto) {
        PrinterDto updated = printerService.updatePrinter(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление принтера (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePrinter(@PathVariable Long id) {
        printerService.deletePrinter(id);
        return ResponseEntity.ok("Successfully");
    }
}