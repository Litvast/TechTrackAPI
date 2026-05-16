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
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuSocketDto;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ProcessorDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.CpuArchitectureMapping;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.CpuSocketMapping;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.ProcessorMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Processor;
import ru.litvast.techtrackapi.repository.equipment.computer.ProcessorRepository;
import ru.litvast.techtrackapi.util.Converter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessorService {

    private final ProcessorRepository processorRepository;
    private final ProcessorMapping processorMapping;
    private final CpuSocketService cpuSocketService;
    private final CpuArchitectureService cpuArchitectureService;
    private final CpuSocketMapping cpuSocketMapping;
    private final CpuArchitectureMapping cpuArchitectureMapping;

    // CREATE
    @Transactional
    public ProcessorDto addProcessor(ProcessorDto processorDto) {
        log.info("=== НАЧАЛО: Добавление процессора ===");
        log.info("Название: {}", processorDto.getName());

        if (processorDto.getId() != null) {
            log.error("Передан ID при создании процессора. ID: {}", processorDto.getId());
            throw new IllegalArgumentException("To create a processor, you must specify a name, not an ID");
        }

        // Сохраняем socket, если он новый
        if (processorDto.getSocket() != null) {
            if (processorDto.getSocket().getId() == null) {
                log.debug("Создание нового сокета: {}", processorDto.getSocket().getName());
                processorDto.setSocket(cpuSocketService.addCpuSocket(processorDto.getSocket()));
            } else {
                log.debug("Использование существующего сокета ID: {}", processorDto.getSocket().getId());
                processorDto.setSocket(cpuSocketService.getCpuSocketById(processorDto.getSocket().getId()));
            }
        }

        // Сохраняем architecture, если она новая
        if (processorDto.getArchitecture() != null) {
            if (processorDto.getArchitecture().getId() == null) {
                log.debug("Создание новой архитектуры: {}", processorDto.getArchitecture().getName());
                processorDto.setArchitecture(cpuArchitectureService.addCpuArchitecture(processorDto.getArchitecture()));
            } else {
                log.debug("Использование существующей архитектуры ID: {}", processorDto.getArchitecture().getId());
                processorDto.setArchitecture(cpuArchitectureService.getCpuArchitectureById(processorDto.getArchitecture().getId()));
            }
        }

        validateAddProcessor(processorDto);

        Processor processor = processorMapping.toEntity(processorDto);
        processorRepository.save(processor);

        log.info("Процессор создан. ID: {}", processor.getId());
        log.info("=== УСПЕШНО: Процессор добавлен ===");

        return processorMapping.toDto(processor);
    }

    // READ all with pagination
    public Page<ProcessorDto> getAllProcessors(Pageable pageable) {
        log.debug("Запрос всех процессоров с пагинацией");

        Page<Processor> processors = processorRepository.findAll(pageable);
        if (processors.isEmpty()) {
            log.warn("Процессоры не найдены");
            throw new NoEntitiesFoundException("No processors found");
        }

        log.debug("Найдено {} процессоров", processors.getTotalElements());
        return processors.map(processorMapping::toDto);
    }

    // READ by id
    public ProcessorDto getProcessorById(Long id) {
        log.debug("Поиск процессора по ID: {}", id);

        Processor processor = processorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Процессор с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Processor with id '%d' not found", id)
                    );
                });

        return processorMapping.toDto(processor);
    }

    // READ by string id
    public ProcessorDto getProcessorByStringId(String stringId) {
        log.debug("Поиск процессора по строковому ID: {}", stringId);
        Long id = Converter.convertIdStringToLong(stringId);
        return getProcessorById(id);
    }

    // READ by name
    public ProcessorDto getProcessorByName(String name) {
        log.debug("Поиск процессора по названию: {}", name);

        Processor processor = processorRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Процессор с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("Processor with name '%s' not found", name)
                    );
                });

        return processorMapping.toDto(processor);
    }

    // UPDATE
    @Transactional
    public ProcessorDto updateProcessor(Long id, ProcessorDto processorDto) {
        log.info("=== НАЧАЛО: Обновление процессора ===");
        log.info("ID процессора: {}", id);

        Processor existingProcessor = processorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Процессор с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Processor with id '%d' not found", id)
                    );
                });

        if (!existingProcessor.getName().equalsIgnoreCase(processorDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingProcessor.getName(), processorDto.getName());

            if (processorRepository.existsByNameIgnoreCase(processorDto.getName())) {
                log.warn("Процессор с названием '{}' уже существует", processorDto.getName());
                throw new IllegalArgumentException(
                        String.format("Processor '%s' is already taken", processorDto.getName())
                );
            }
            existingProcessor.setName(processorDto.getName());
        }

        // Обновляем socket
        if (processorDto.getSocket() != null) {
            if (processorDto.getSocket().getId() != null) {
                log.debug("Обновление сокета по ID: {}", processorDto.getSocket().getId());
                CpuSocketDto existingSocket = cpuSocketService.getCpuSocketById(processorDto.getSocket().getId());
                processorDto.setSocket(existingSocket);
            } else {
                log.debug("Создание нового сокета: {}", processorDto.getSocket().getName());
                CpuSocketDto newSocket = cpuSocketService.addCpuSocket(processorDto.getSocket());
                processorDto.setSocket(newSocket);
            }
        } else {
            processorDto.setSocket(existingProcessor.getSocket() != null
                    ? cpuSocketService.getCpuSocketById(existingProcessor.getSocket().getId())
                    : null);
        }

        // Обновляем architecture
        if (processorDto.getArchitecture() != null) {
            if (processorDto.getArchitecture().getId() != null) {
                log.debug("Обновление архитектуры по ID: {}", processorDto.getArchitecture().getId());
                CpuArchitectureDto existingArchitecture = cpuArchitectureService.getCpuArchitectureById(processorDto.getArchitecture().getId());
                processorDto.setArchitecture(existingArchitecture);
            } else {
                log.debug("Создание новой архитектуры: {}", processorDto.getArchitecture().getName());
                CpuArchitectureDto newArchitecture = cpuArchitectureService.addCpuArchitecture(processorDto.getArchitecture());
                processorDto.setArchitecture(newArchitecture);
            }
        } else {
            processorDto.setArchitecture(existingProcessor.getArchitecture() != null
                    ? cpuArchitectureService.getCpuArchitectureById(existingProcessor.getArchitecture().getId())
                    : null);
        }

        // Обновляем поля
        if (processorDto.getManufacturer() != null) {
            existingProcessor.setManufacturer(processorDto.getManufacturer());
        }
        if (processorDto.getClockFrequencyGHz() != null) {
            existingProcessor.setClockFrequencyGHz(processorDto.getClockFrequencyGHz());
        }
        if (processorDto.getTurboClockFrequencyGHz() != null) {
            existingProcessor.setTurboClockFrequencyGHz(processorDto.getTurboClockFrequencyGHz());
        }
        if (processorDto.getNumberOfCores() != null) {
            existingProcessor.setNumberOfCores(processorDto.getNumberOfCores());
        }
        if (processorDto.getNumberOfThreads() != null) {
            existingProcessor.setNumberOfThreads(processorDto.getNumberOfThreads());
        }
        if (processorDto.getL1CacheKB() != null) {
            existingProcessor.setL1CacheKB(processorDto.getL1CacheKB());
        }
        if (processorDto.getL2CacheKB() != null) {
            existingProcessor.setL2CacheKB(processorDto.getL2CacheKB());
        }
        if (processorDto.getL3CacheMB() != null) {
            existingProcessor.setL3CacheMB(processorDto.getL3CacheMB());
        }
        if (processorDto.getTdpWatts() != null) {
            existingProcessor.setTdpWatts(processorDto.getTdpWatts());
        }
        if (processorDto.getLithographyNm() != null) {
            existingProcessor.setLithographyNm(processorDto.getLithographyNm());
        }

        if (processorDto.getSocket() != null) {
            existingProcessor.setSocket(cpuSocketMapping.toEntity(processorDto.getSocket()));
        }
        if (processorDto.getArchitecture() != null) {
            existingProcessor.setArchitecture(cpuArchitectureMapping.toEntity(processorDto.getArchitecture()));
        }

        processorRepository.save(existingProcessor);
        log.info("=== УСПЕШНО: Процессор обновлён ===");

        return processorMapping.toDto(existingProcessor);
    }

    // DELETE
    @Transactional
    public void deleteProcessor(Long id) {
        log.info("=== НАЧАЛО: Удаление процессора ===");
        log.info("ID процессора: {}", id);

        if (!processorRepository.existsById(id)) {
            log.error("Процессор с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Processor with id '%d' not found", id)
            );
        }

        processorRepository.deleteById(id);
        log.info("=== УСПЕШНО: Процессор удалён ===");
    }

    // Validation
    public void validateAddProcessor(ProcessorDto processorDto) {
        if (processorRepository.existsByNameIgnoreCase(processorDto.getName())) {
            log.warn("Процессор с названием '{}' уже существует", processorDto.getName());
            throw new IllegalArgumentException(
                    String.format("Processor '%s' is already taken", processorDto.getName())
            );
        }
    }
}