package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.EmployeeDto;
import ru.litvast.techtrackapi.model.dto.EmployeeUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.EmployeeMapping;
import ru.litvast.techtrackapi.model.entity.Employee;
import ru.litvast.techtrackapi.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapping employeeMapping;

    // CREATE
    @Transactional
    public EmployeeDto addEmployee(EmployeeDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create an employee, you must specify a name, not an ID");
        }

        validateAddEmployee(dto);

        Employee employee = employeeMapping.toEntity(dto);
        employeeRepository.save(employee);
        return employeeMapping.toDto(employee);
    }

    // READ all
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        if (employees.isEmpty()) {
            throw new NoEntitiesFoundException("No employees found");
        }
        return employees.map(employeeMapping::toDto);
    }

    // READ by id
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Employee with id '%d' not found", id)
                ));
        return employeeMapping.toDto(employee);
    }

    // READ by full name
    public EmployeeDto getEmployeeByFullName(String fullName) {
        Employee employee = employeeRepository.findByFullNameIgnoreCase(fullName)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Employee with name '%s' not found", fullName)
                ));
        return employeeMapping.toDto(employee);
    }

    // READ by email
    public EmployeeDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Employee with email '%s' not found", email)
                ));
        return employeeMapping.toDto(employee);
    }

    // UPDATE
    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeUpdateDto dto) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Employee with id '%d' not found", id)
                ));

        if (dto.getFullName() != null && !existingEmployee.getFullName().equalsIgnoreCase(dto.getFullName())) {
            if (employeeRepository.existsByFullNameIgnoreCase(dto.getFullName())) {
                throw new IllegalArgumentException(
                        String.format("Employee '%s' is already taken", dto.getFullName())
                );
            }
            existingEmployee.setFullName(dto.getFullName());
        }

        if (dto.getPosition() != null) existingEmployee.setPosition(dto.getPosition());
        if (dto.getEmail() != null) existingEmployee.setEmail(dto.getEmail());
        if (dto.getPhone() != null) existingEmployee.setPhone(dto.getPhone());

        employeeRepository.save(existingEmployee);
        return employeeMapping.toDto(existingEmployee);
    }

    // DELETE
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Employee with id '%d' not found", id)
            );
        }
        employeeRepository.deleteById(id);
    }

    // Validation
    public void validateAddEmployee(EmployeeDto dto) {
        if (employeeRepository.existsByFullNameIgnoreCase(dto.getFullName())) {
            throw new IllegalArgumentException(
                    String.format("Employee '%s' is already taken", dto.getFullName())
            );
        }
        if (dto.getEmail() != null && employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Employee with email '%s' already exists", dto.getEmail())
            );
        }
    }
}