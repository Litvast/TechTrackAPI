package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuArchitectureDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.CpuArchitectureMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuArchitecture;
import ru.litvast.techtrackapi.repository.equipment.computer.CpuArchitectureRepository;
import ru.litvast.techtrackapi.repository.equipment.computer.CpuSocketRepository;
import ru.litvast.techtrackapi.util.Converter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CpuArchitectureService {

    private final CpuArchitectureRepository architectureRepository;
    private final CpuArchitectureMapping architectureMapping;
    private final CpuSocketRepository cpuSocketRepository;

    // CREATE
    @Transactional
    public CpuArchitectureDto addCpuArchitecture(CpuArchitectureDto architectureDto) {
        log.info("=== НАЧАЛО: Добавление архитектуры процессора ===");
        log.info("Название: {}", architectureDto.getName());

        if (architectureDto.getId() != null) {
            log.error("Передан ID при создании архитектуры. ID: {}", architectureDto.getId());
            throw new IllegalArgumentException("To create an architecture, you must specify a name, not an ID");
        }

        validateAddCpuArchitecture(architectureDto);

        CpuArchitecture architecture = architectureMapping.toEntity(architectureDto);
        architectureRepository.save(architecture);

        log.info("Архитектура создана. ID: {}", architecture.getId());
        log.info("=== УСПЕШНО: Архитектура добавлена ===");

        return architectureMapping.toDto(architecture);
    }

    // READ all with pagination
    public Page<CpuArchitectureDto> getAllCpuArchitectures(Pageable pageable) {
        log.debug("Запрос всех архитектур процессоров с пагинацией");

        Page<CpuArchitecture> architectures = architectureRepository.findAll(pageable);
        if (architectures.isEmpty()) {
            log.warn("Архитектуры не найдены");
            throw new NoEntitiesFoundException("No CPU architectures found");
        }

        log.debug("Найдено {} архитектур", architectures.getTotalElements());
        return architectures.map(architectureMapping::toDto);
    }

    // READ by id
    public CpuArchitectureDto getCpuArchitectureById(Long id) {
        log.debug("Поиск архитектуры по ID: {}", id);

        CpuArchitecture architecture = architectureRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Архитектура с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("CpuArchitecture with id '%d' not found", id)
                    );
                });

        return architectureMapping.toDto(architecture);
    }

    // READ by string id
    public CpuArchitectureDto getCpuArchitectureByStringId(String stringId) {
        log.debug("Поиск архитектуры по строковому ID: {}", stringId);
        Long id = Converter.convertIdStringToLong(stringId);
        return getCpuArchitectureById(id);
    }

    // READ by name
    public CpuArchitectureDto getCpuArchitectureByName(String name) {
        log.debug("Поиск архитектуры по названию: {}", name);

        CpuArchitecture architecture = architectureRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Архитектура с названием '{}' не найдена", name);
                    return new EntityNotFoundException(
                            String.format("CpuArchitecture with name '%s' not found", name)
                    );
                });

        return architectureMapping.toDto(architecture);
    }

    // UPDATE
    @Transactional
    public CpuArchitectureDto updateCpuArchitecture(Long id, CpuArchitectureDto architectureDto) {
        log.info("=== НАЧАЛО: Обновление архитектуры процессора ===");
        log.info("ID архитектуры: {}", id);

        CpuArchitecture existingArchitecture = architectureRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Архитектура с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("CpuArchitecture with id '%d' not found", id)
                    );
                });

        if (!existingArchitecture.getName().equalsIgnoreCase(architectureDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingArchitecture.getName(), architectureDto.getName());

            if (architectureRepository.existsByNameIgnoreCase(architectureDto.getName())) {
                log.warn("Архитектура с названием '{}' уже существует", architectureDto.getName());
                throw new IllegalArgumentException(
                        String.format("CpuArchitecture '%s' is already taken", architectureDto.getName())
                );
            }
            existingArchitecture.setName(architectureDto.getName());
        }

        if (architectureDto.getBitWidth() != null) {
            log.info("Изменение разрядности: {} -> {}", existingArchitecture.getBitWidth(), architectureDto.getBitWidth());
            existingArchitecture.setBitWidth(architectureDto.getBitWidth());
        }

        architectureRepository.save(existingArchitecture);
        log.info("=== УСПЕШНО: Архитектура обновлена ===");

        return architectureMapping.toDto(existingArchitecture);
    }

    // DELETE
    @Transactional
    public void deleteCpuArchitecture(Long id) {
        log.info("=== НАЧАЛО: Удаление архитектуры процессора ===");
        log.info("ID архитектуры: {}", id);

        if (!architectureRepository.existsById(id)) {
            log.error("Архитектура с ID {} не найдена", id);
            throw new EntityNotFoundException(
                    String.format("CpuArchitecture with id '%d' not found", id)
            );
        }

        architectureRepository.deleteById(id);
        log.info("=== УСПЕШНО: Архитектура удалена ===");
    }

    // Validation
    public void validateAddCpuArchitecture(CpuArchitectureDto architectureDto) {
        if (architectureRepository.existsByNameIgnoreCase(architectureDto.getName())) {
            log.warn("Архитектура с названием '{}' уже существует", architectureDto.getName());
            throw new IllegalArgumentException(
                    String.format("CpuArchitecture '%s' is already taken", architectureDto.getName())
            );
        }
    }
}