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
            description = "Создаёт нового сотрудника с указанными данными. Доступно только для администраторов. Возвращает созданный объект EmployeeDto.",
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
            description = "Возвращает страницу со списком всех сотрудников с поддержкой пагинации и сортировки по полному имени.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllEmployees(@PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employees);
    }

    @Operation(
            summary = "Поиск сотрудника по id",
            description = "Возвращает сотрудника по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Поиск сотрудника по ФИО",
            description = "Возвращает сотрудника по его полному имени (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{fullName}")
    public ResponseEntity<?> getEmployeeByFullName(@PathVariable String fullName) {
        EmployeeDto employee = employeeService.getEmployeeByFullName(fullName);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Поиск сотрудника по email",
            description = "Возвращает сотрудника по его электронной почте (уникальное поле).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email) {
        EmployeeDto employee = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Поиск сотрудника по имени пользователя",
            description = "Возвращает сотрудника, привязанного к указанному имени пользователя (username).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-user-username/{userUsername}")
    public ResponseEntity<?> getEmployeeByUserUsername(@PathVariable("userUsername") String userUsername) {
        EmployeeDto employee = employeeService.getEmployeeByUserUsername(userUsername);
        return ResponseEntity.ok(employee);
    }

    @Operation(
            summary = "Подсчёт общего количества сотрудников",
            description = "Возвращает общее количество сотрудников в системе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountEmployees() {
        long countEmployees = employeeService.getCountEmployees();
        return ResponseEntity.ok(countEmployees);
    }

    @Operation(
            summary = "Подсчёт количества сотрудников по компании",
            description = "Возвращает количество сотрудников, принадлежащих указанной компании.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count/by-company-id/{companyId}")
    public ResponseEntity<?> getCountEmployeesByCompanyId(@PathVariable("companyId") long companyId) {
        long countEmployees = employeeService.getCountEmployeesByCompanyId(companyId);
        return ResponseEntity.ok(countEmployees);
    }

    @Operation(
            summary = "Обновление сотрудника (ADMIN)",
            description = "Обновляет данные существующего сотрудника по его id. Доступно только для администраторов. Возвращает обновлённый объект EmployeeDto.",
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
            summary = "Удаление сотрудника (ADMIN)",
            description = "Удаляет сотрудника по его id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}