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
import ru.litvast.techtrackapi.model.dto.EmployeeDto;
import ru.litvast.techtrackapi.model.dto.EmployeeUpdateDto;
import ru.litvast.techtrackapi.service.EmployeeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
@Tag(name = "employees", description = "Методы для работы с сотрудниками")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Добавление сотрудника (ADMIN)",
            description = "В ответ выдаётся созданный объект EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody EmployeeDto dto) {
        EmployeeDto created = employeeService.addEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех сотрудников с пагинацией",
            description = "В ответ выдаётся страница с объектами EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllEmployees(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employees);
    }

    @Operation(
            summary = "Поиск сотрудника по id",
            description = "В ответ выдаётся найденный объект EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Поиск сотрудника по ФИО",
            description = "В ответ выдаётся найденный объект EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{fullName}")
    public ResponseEntity<?> getEmployeeByFullName(@PathVariable String fullName) {
        EmployeeDto employee = employeeService.getEmployeeByFullName(fullName);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Поиск сотрудника по email",
            description = "В ответ выдаётся найденный объект EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email) {
        EmployeeDto employee = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Поиск сотрудника по юзернейму пользователя",
            description = "В ответ выдаётся найденный объект EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-user-username/{userUsername}")
    public ResponseEntity<?> getEmployeeByUserUsername(@PathVariable("userUsername") String userUsername) {
        EmployeeDto employee = employeeService.getEmployeeByUserUsername(userUsername);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Подсчёт общего количества сотрудников",
            description = "В ответ выдаётся подсчитанное количество сотрудников.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountEmployees() {
        long countEmployees = employeeService.getCountEmployees();
        return ResponseEntity.ok(countEmployees);
    }

    @Operation(
            summary = "Подсчёт количества сотрудников по айди компании",
            description = "В ответ выдаётся подсчитанное количество сотрудников.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count/by-company-id/{companyId}")
    public ResponseEntity<?> getCountEmployeesByCompanyId(@PathVariable("companyId") long companyId) {
        long countEmployees = employeeService.getCountEmployeesByCompanyId(companyId);
        return ResponseEntity.ok(countEmployees);
    }

    @Operation(
            summary = "Обновление сотрудника по id (ADMIN)",
            description = "В ответ выдаётся обновлённый объект EmployeeDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                            @Valid @RequestBody EmployeeUpdateDto dto) {
        EmployeeDto updated = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление сотрудника по id (ADMIN)",
            description = "В ответ выдаётся сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Successfully");
    }
}