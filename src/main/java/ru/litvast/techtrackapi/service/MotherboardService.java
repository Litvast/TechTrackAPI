package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.*;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.*;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Motherboard;
import ru.litvast.techtrackapi.repository.equipment.computer.MotherboardRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MotherboardService {

    private final MotherboardRepository motherboardRepository;
    private final MotherboardMapping motherboardMapping;
    private final MotherboardFormFactorService formFactorService;
    private final CpuSocketService cpuSocketService;
    private final MotherboardFormFactorMapping motherboardFormFactorMapping;
    private final CpuSocketMapping cpuSocketMapping;
    private final MemorySupportMapping memorySupportMapping;
    private final StoragePortMapping storagePortMapping;
    private final IoPortMapping ioPortMapping;

    // CREATE
    @Transactional
    public MotherboardDto addMotherboard(MotherboardDto dto) {
        log.info("=== НАЧАЛО: Добавление материнской платы ===");
        log.info("Название: {}", dto.getName());

        if (dto.getId() != null) {
            log.error("Передан ID при создании материнской платы. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a motherboard, you must specify a name, not an ID");
        }

        // Сохраняем formFactor, если новый
        if (dto.getFormFactor() != null && dto.getFormFactor().getId() == null) {
            log.debug("Создание нового форм-фактора: {}", dto.getFormFactor().getCode());
            MotherboardFormFactorDto saved = formFactorService.addFormFactor(dto.getFormFactor());
            dto.setFormFactor(saved);
        }

        // Сохраняем socket, если новый
        if (dto.getSocket() != null && dto.getSocket().getId() == null) {
            log.debug("Создание нового сокета: {}", dto.getSocket().getName());
            CpuSocketDto saved = cpuSocketService.addCpuSocket(dto.getSocket());
            dto.setSocket(saved);
        }

        validateAddMotherboard(dto);

        Motherboard entity = motherboardMapping.toEntity(dto);
        motherboardRepository.save(entity);

        log.info("Материнская плата создана. ID: {}", entity.getId());
        log.info("=== УСПЕШНО: Материнская плата добавлена ===");

        return motherboardMapping.toDto(entity);
    }

    // READ all with pagination
    public Page<MotherboardDto> getAllMotherboards(Pageable pageable) {
        log.debug("Запрос всех материнских плат с пагинацией");

        Page<Motherboard> entities = motherboardRepository.findAll(pageable);
        if (entities.isEmpty()) {
            log.warn("Материнские платы не найдены");
            throw new NoEntitiesFoundException("No motherboards found");
        }

        log.debug("Найдено {} материнских плат", entities.getTotalElements());
        return entities.map(motherboardMapping::toDto);
    }

    // READ by id
    public MotherboardDto getMotherboardById(Long id) {
        log.debug("Поиск материнской платы по ID: {}", id);

        Motherboard entity = motherboardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Материнская плата с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Motherboard with id '%d' not found", id)
                    );
                });

        return motherboardMapping.toDto(entity);
    }

    // READ by name
    public MotherboardDto getMotherboardByName(String name) {
        log.debug("Поиск материнской платы по названию: {}", name);

        Motherboard entity = motherboardRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Материнская плата с названием '{}' не найдена", name);
                    return new EntityNotFoundException(
                            String.format("Motherboard with name '%s' not found", name)
                    );
                });

        return motherboardMapping.toDto(entity);
    }

    // UPDATE
    @Transactional
    public MotherboardDto updateMotherboard(Long id, MotherboardDto dto) {
        log.info("=== НАЧАЛО: Обновление материнской платы ===");
        log.info("ID материнской платы: {}", id);

        Motherboard existing = motherboardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Материнская плата с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Motherboard with id '%d' not found", id)
                    );
                });

        if (!existing.getName().equalsIgnoreCase(dto.getName())) {
            log.info("Изменение названия: {} -> {}", existing.getName(), dto.getName());

            if (motherboardRepository.existsByNameIgnoreCase(dto.getName())) {
                log.warn("Материнская плата с названием '{}' уже существует", dto.getName());
                throw new IllegalArgumentException(
                        String.format("Motherboard '%s' is already taken", dto.getName())
                );
            }
            existing.setName(dto.getName());
        }

        // Обновляем formFactor
        if (dto.getFormFactor() != null) {
            if (dto.getFormFactor().getId() != null) {
                log.debug("Обновление форм-фактора по ID: {}", dto.getFormFactor().getId());
                existing.setFormFactor(motherboardFormFactorMapping.toEntity(formFactorService.getFormFactorById(dto.getFormFactor().getId())));
            } else {
                log.debug("Создание нового форм-фактора: {}", dto.getFormFactor().getCode());
                MotherboardFormFactorDto saved = formFactorService.addFormFactor(dto.getFormFactor());
                existing.setFormFactor(motherboardFormFactorMapping.toEntity(formFactorService.getFormFactorById(saved.getId())));
            }
        }

        // Обновляем socket
        if (dto.getSocket() != null) {
            if (dto.getSocket().getId() != null) {
                log.debug("Обновление сокета по ID: {}", dto.getSocket().getId());
                existing.setSocket(cpuSocketMapping.toEntity(cpuSocketService.getCpuSocketById(dto.getSocket().getId())));
            } else {
                log.debug("Создание нового сокета: {}", dto.getSocket().getName());
                CpuSocketDto saved = cpuSocketService.addCpuSocket(dto.getSocket());
                existing.setSocket(cpuSocketMapping.toEntity(cpuSocketService.getCpuSocketById(saved.getId())));
            }
        }

        // Обновляем простые поля
        if (dto.getManufacturer() != null) {
            log.debug("Обновление производителя: {}", dto.getManufacturer());
            existing.setManufacturer(dto.getManufacturer());
        }
        if (dto.getChipset() != null) {
            log.debug("Обновление чипсета: {}", dto.getChipset());
            existing.setChipset(dto.getChipset());
        }

        existing.setMemorySupports(memorySupportMapping.toEntityList(dto.getMemorySupports()));
        existing.setStoragePorts(storagePortMapping.toEntityList(dto.getStoragePorts()));
        existing.setIoPorts(ioPortMapping.toEntityList(dto.getIoPorts()));

        motherboardRepository.save(existing);
        log.info("=== УСПЕШНО: Материнская плата обновлена ===");

        return motherboardMapping.toDto(existing);
    }

    // DELETE
    @Transactional
    public void deleteMotherboard(Long id) {
        log.info("=== НАЧАЛО: Удаление материнской платы ===");
        log.info("ID материнской платы: {}", id);

        if (!motherboardRepository.existsById(id)) {
            log.error("Материнская плата с ID {} не найдена", id);
            throw new EntityNotFoundException(
                    String.format("Motherboard with id '%d' not found", id)
            );
        }

        motherboardRepository.deleteById(id);
        log.info("=== УСПЕШНО: Материнская плата удалена ===");
    }

    // Validation
    public void validateAddMotherboard(MotherboardDto dto) {
        if (motherboardRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Материнская плата с названием '{}' уже существует", dto.getName());
            throw new IllegalArgumentException(
                    String.format("Motherboard '%s' is already taken", dto.getName())
            );
        }
    }
}