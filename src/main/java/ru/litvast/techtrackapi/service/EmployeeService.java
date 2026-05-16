package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.litvast.techtrackapi.model.entity.Room;
import ru.litvast.techtrackapi.model.entity.User;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;
import ru.litvast.techtrackapi.model.entity.equipment.Printer;
import ru.litvast.techtrackapi.repository.EmployeeRepository;
import ru.litvast.techtrackapi.repository.RoomRepository;
import ru.litvast.techtrackapi.repository.UserRepository;
import ru.litvast.techtrackapi.repository.equipment.computer.ComputerRepository;
import ru.litvast.techtrackapi.repository.equipment.PrinterRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapping employeeMapping;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ComputerRepository computerRepository;
    private final PrinterRepository printerRepository;

    // CREATE
    @Transactional
    public EmployeeDto addEmployee(EmployeeDto dto) {
        log.info("=== НАЧАЛО: Добавление сотрудника ===");
        log.info("ФИО: {}, Email: {}", dto.getFullName(), dto.getEmail());

        if (dto.getId() != null) {
            log.error("Передан ID при создании сотрудника. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create an employee, you must specify a name, not an ID");
        }

        validateAddEmployee(dto);

        Employee employee = employeeMapping.toEntity(dto);

        if (dto.getUserId() != null) {
            log.debug("Привязка к пользователю ID: {}", dto.getUserId());
            User user = userRepository.findById(dto.getUserId().intValue())
                    .orElseThrow(() -> {
                        log.error("Пользователь с ID {} не найден", dto.getUserId());
                        return new EntityNotFoundException(
                                String.format("User with id '%d' not found", dto.getUserId())
                        );
                    });
            employee.setUser(user);
        }

        if (dto.getRoomId() != null) {
            log.debug("Привязка к комнате ID: {}", dto.getRoomId());
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> {
                        log.error("Комната с ID {} не найдена", dto.getRoomId());
                        return new EntityNotFoundException(
                                String.format("Room with id '%d' not found", dto.getRoomId())
                        );
                    });
            employee.setRoom(room);
        }

        if (dto.getAssignedComputerId() != null) {
            log.debug("Привязка к компьютеру ID: {}", dto.getAssignedComputerId());
            Computer computer = computerRepository.findById(dto.getAssignedComputerId())
                    .orElseThrow(() -> {
                        log.error("Компьютер с ID {} не найден", dto.getAssignedComputerId());
                        return new EntityNotFoundException(
                                String.format("Computer with id '%d' not found", dto.getAssignedComputerId())
                        );
                    });
            employee.setAssignedComputer(computer);
        }

        if (dto.getAssignedPrinterId() != null) {
            log.debug("Привязка к принтеру ID: {}", dto.getAssignedPrinterId());
            Printer printer = printerRepository.findById(dto.getAssignedPrinterId())
                    .orElseThrow(() -> {
                        log.error("Принтер с ID {} не найден", dto.getAssignedPrinterId());
                        return new EntityNotFoundException(
                                String.format("Printer with id '%d' not found", dto.getAssignedPrinterId())
                        );
                    });
            employee.setAssignedPrinter(printer);
        }

        employeeRepository.save(employee);
        log.info("Сотрудник создан. ID: {}", employee.getId());
        log.info("=== УСПЕШНО: Сотрудник добавлен ===");

        return employeeMapping.toDto(employee);
    }

    // READ all
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        log.debug("Запрос всех сотрудников с пагинацией");

        Page<Employee> employees = employeeRepository.findAll(pageable);
        if (employees.isEmpty()) {
            log.warn("Сотрудники не найдены");
            throw new NoEntitiesFoundException("No employees found");
        }

        log.debug("Найдено {} сотрудников", employees.getTotalElements());
        return employees.map(employeeMapping::toDto);
    }

    // READ by id
    public EmployeeDto getEmployeeById(Long id) {
        log.debug("Поиск сотрудника по ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Сотрудник с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Employee with id '%d' not found", id)
                    );
                });

        return employeeMapping.toDto(employee);
    }

    // READ by full name
    public EmployeeDto getEmployeeByFullName(String fullName) {
        log.debug("Поиск сотрудника по ФИО: {}", fullName);

        Employee employee = employeeRepository.findByFullNameIgnoreCase(fullName)
                .orElseThrow(() -> {
                    log.error("Сотрудник с ФИО '{}' не найден", fullName);
                    return new EntityNotFoundException(
                            String.format("Employee with name '%s' not found", fullName)
                    );
                });

        return employeeMapping.toDto(employee);
    }

    // READ by email
    public EmployeeDto getEmployeeByEmail(String email) {
        log.debug("Поиск сотрудника по email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Сотрудник с email '{}' не найден", email);
                    return new EntityNotFoundException(
                            String.format("Employee with email '%s' not found", email)
                    );
                });

        return employeeMapping.toDto(employee);
    }

    // UPDATE
    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление сотрудника ===");
        log.info("ID сотрудника: {}", id);

        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Сотрудник с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Employee with id '%d' not found", id)
                    );
                });

        if (dto.getFullName() != null && !existingEmployee.getFullName().equalsIgnoreCase(dto.getFullName())) {
            log.info("Изменение ФИО: {} -> {}", existingEmployee.getFullName(), dto.getFullName());

            if (employeeRepository.existsByFullNameIgnoreCase(dto.getFullName())) {
                log.warn("Сотрудник с ФИО '{}' уже существует", dto.getFullName());
                throw new IllegalArgumentException(
                        String.format("Employee '%s' is already taken", dto.getFullName())
                );
            }
            existingEmployee.setFullName(dto.getFullName());
        }

        if (dto.getPosition() != null) {
            log.info("Изменение должности: {} -> {}", existingEmployee.getPosition(), dto.getPosition());
            existingEmployee.setPosition(dto.getPosition());
        }

        if (dto.getEmail() != null) {
            log.info("Изменение email: {} -> {}", existingEmployee.getEmail(), dto.getEmail());
            existingEmployee.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            log.info("Изменение телефона: {} -> {}", existingEmployee.getPhone(), dto.getPhone());
            existingEmployee.setPhone(dto.getPhone());
        }

        if (dto.getUserId() != null) {
            log.debug("Обновление привязки к пользователю ID: {}", dto.getUserId());
            User user = userRepository.findById(dto.getUserId().intValue())
                    .orElseThrow(() -> {
                        log.error("Пользователь с ID {} не найден", dto.getUserId());
                        return new EntityNotFoundException(
                                String.format("User with id '%d' not found", dto.getUserId())
                        );
                    });
            existingEmployee.setUser(user);
        }

        if (dto.getRoomId() != null) {
            log.debug("Обновление привязки к комнате ID: {}", dto.getRoomId());
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> {
                        log.error("Комната с ID {} не найдена", dto.getRoomId());
                        return new EntityNotFoundException(
                                String.format("Room with id '%d' not found", dto.getRoomId())
                        );
                    });
            existingEmployee.setRoom(room);
        }

        if (dto.getAssignedComputerId() != null) {
            log.debug("Обновление привязки к компьютеру ID: {}", dto.getAssignedComputerId());
            Computer computer = computerRepository.findById(dto.getAssignedComputerId())
                    .orElseThrow(() -> {
                        log.error("Компьютер с ID {} не найден", dto.getAssignedComputerId());
                        return new EntityNotFoundException(
                                String.format("Computer with id '%d' not found", dto.getAssignedComputerId())
                        );
                    });
            existingEmployee.setAssignedComputer(computer);
        }

        if (dto.getAssignedPrinterId() != null) {
            log.debug("Обновление привязки к принтеру ID: {}", dto.getAssignedPrinterId());
            Printer printer = printerRepository.findById(dto.getAssignedPrinterId())
                    .orElseThrow(() -> {
                        log.error("Принтер с ID {} не найден", dto.getAssignedPrinterId());
                        return new EntityNotFoundException(
                                String.format("Printer with id '%d' not found", dto.getAssignedPrinterId())
                        );
                    });
            existingEmployee.setAssignedPrinter(printer);
        }

        employeeRepository.save(existingEmployee);
        log.info("=== УСПЕШНО: Сотрудник обновлён ===");

        return employeeMapping.toDto(existingEmployee);
    }

    // DELETE
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("=== НАЧАЛО: Удаление сотрудника ===");
        log.info("ID сотрудника: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.error("Сотрудник с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Employee with id '%d' not found", id)
            );
        }

        employeeRepository.deleteById(id);
        log.info("=== УСПЕШНО: Сотрудник удалён ===");
    }

    // Validation
    public void validateAddEmployee(EmployeeDto dto) {
        if (employeeRepository.existsByFullNameIgnoreCase(dto.getFullName())) {
            log.warn("Сотрудник с ФИО '{}' уже существует", dto.getFullName());
            throw new IllegalArgumentException(
                    String.format("Employee '%s' is already taken", dto.getFullName())
            );
        }
        if (dto.getEmail() != null && employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Сотрудник с email '{}' уже существует", dto.getEmail());
            throw new IllegalArgumentException(
                    String.format("Employee with email '%s' already exists", dto.getEmail())
            );
        }
    }
}