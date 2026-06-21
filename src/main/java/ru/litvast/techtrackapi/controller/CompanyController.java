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
import ru.litvast.techtrackapi.model.dto.CompanyDto;
import ru.litvast.techtrackapi.model.dto.CompanyUpdateDto;
import ru.litvast.techtrackapi.service.CompanyService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/companies")
@Tag(name = "companies", description = "Методы для работы с компаниями")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "Добавление компании (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addCompany(@Valid @RequestBody CompanyDto dto) {
        CompanyDto created = companyService.addCompany(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех компаний с пагинацией",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllCompanies(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<CompanyDto> companies = companyService.getAllCompanies(pageable);
        return ResponseEntity.ok(companies);
    }

    @Operation(
            summary = "Поиск компании по id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        CompanyDto company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @Operation(
            summary = "Поиск компании по названию",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getCompanyByName(@PathVariable String name) {
        CompanyDto company = companyService.getCompanyByName(name);
        return ResponseEntity.ok(company);
    }

    @Operation(
            summary = "Поиск компании по ИНН",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-inn/{inn}")
    public ResponseEntity<?> getCompanyByInn(@PathVariable String inn) {
        CompanyDto company = companyService.getCompanyByInn(inn);
        return ResponseEntity.ok(company);
    }

    @Operation(
            summary = "Поиск компании по айди кабинета",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-room-id/{roomId}")
    public ResponseEntity<?> getCompanyByRoomId(@PathVariable("roomId") long roomId) {
        CompanyDto company = companyService.getCompanyByRoomId(roomId);
        return ResponseEntity.ok(company);
    }

    @Operation(
            summary = "Подсчёт общего количества компаний",
            description = "В ответ выдаётся подсчитанное количество компаний",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountCompanies() {
        long count = companyService.getCountCompanies();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Обновление компании (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCompany(@PathVariable Long id,
                                           @Valid @RequestBody CompanyUpdateDto dto) {
        CompanyDto updated = companyService.updateCompany(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление компании (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Successfully");
    }
}