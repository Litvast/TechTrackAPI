package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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
        if (processorDto.getId() != null) {
            throw new IllegalArgumentException("To create a processor, you must specify a name, not an ID");
        }

        // Сохраняем socket, если он новый
        if (processorDto.getSocket() != null) {
            processorDto.setSocket(processorDto.getSocket().getId() == null
                    ? cpuSocketService.addCpuSocket(processorDto.getSocket())
                    : cpuSocketService.getCpuSocketById(processorDto.getSocket().getId()));
        }

        // Сохраняем architecture, если она новая
        if (processorDto.getArchitecture() != null) {
            processorDto.setArchitecture(processorDto.getArchitecture().getId() == null
                    ? cpuArchitectureService.addCpuArchitecture(processorDto.getArchitecture())
                    : cpuArchitectureService.getCpuArchitectureById(processorDto.getArchitecture().getId()));
        }

        validateAddProcessor(processorDto);

        Processor processor = processorMapping.toEntity(processorDto);
        processorRepository.save(processor);
        return processorMapping.toDto(processor);
    }

    // READ all with pagination
    public Page<ProcessorDto> getAllProcessors(Pageable pageable) {
        Page<Processor> processors = processorRepository.findAll(pageable);
        if (processors.isEmpty()) {
            throw new NoEntitiesFoundException("No processors found");
        }
        return processors.map(processorMapping::toDto);
    }

    // READ by id
    public ProcessorDto getProcessorById(Long id) {
        Processor processor = processorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Processor with id '%d' not found", id)
                ));
        return processorMapping.toDto(processor);
    }

    // READ by string id
    public ProcessorDto getProcessorByStringId(String stringId) {
        Long id = Converter.convertIdStringToLong(stringId);
        return getProcessorById(id);
    }

    // READ by name
    public ProcessorDto getProcessorByName(String name) {
        Processor processor = processorRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Processor with name '%s' not found", name)
                ));
        return processorMapping.toDto(processor);
    }

    // UPDATE
    @Transactional
    public ProcessorDto updateProcessor(Long id, ProcessorDto processorDto) {
        Processor existingProcessor = processorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Processor with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingProcessor.getName().equalsIgnoreCase(processorDto.getName())) {
            if (processorRepository.existsByNameIgnoreCase(processorDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Processor '%s' is already taken", processorDto.getName())
                );
            }
        }

        // Обновляем socket
        if (processorDto.getSocket() != null) {
            if (processorDto.getSocket().getId() != null) {
                CpuSocketDto existingSocket = cpuSocketService.getCpuSocketById(processorDto.getSocket().getId());
                processorDto.setSocket(existingSocket);
            } else {
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
                CpuArchitectureDto existingArchitecture = cpuArchitectureService.getCpuArchitectureById(processorDto.getArchitecture().getId());
                processorDto.setArchitecture(existingArchitecture);
            } else {
                CpuArchitectureDto newArchitecture = cpuArchitectureService.addCpuArchitecture(processorDto.getArchitecture());
                processorDto.setArchitecture(newArchitecture);
            }
        } else {
            processorDto.setArchitecture(existingProcessor.getArchitecture() != null
                    ? cpuArchitectureService.getCpuArchitectureById(existingProcessor.getArchitecture().getId())
                    : null);
        }

        // Обновляем поля
        existingProcessor.setName(processorDto.getName());
        existingProcessor.setManufacturer(processorDto.getManufacturer());
        existingProcessor.setClockFrequencyGHz(processorDto.getClockFrequencyGHz());
        existingProcessor.setTurboClockFrequencyGHz(processorDto.getTurboClockFrequencyGHz());
        existingProcessor.setNumberOfCores(processorDto.getNumberOfCores());
        existingProcessor.setNumberOfThreads(processorDto.getNumberOfThreads());
        existingProcessor.setL1CacheKB(processorDto.getL1CacheKB());
        existingProcessor.setL2CacheKB(processorDto.getL2CacheKB());
        existingProcessor.setL3CacheMB(processorDto.getL3CacheMB());
        existingProcessor.setTdpWatts(processorDto.getTdpWatts());
        existingProcessor.setLithographyNm(processorDto.getLithographyNm());

        if (processorDto.getSocket() != null) {
            existingProcessor.setSocket(cpuSocketMapping.toEntity(processorDto.getSocket()));
        }
        if (processorDto.getArchitecture() != null) {
            existingProcessor.setArchitecture(cpuArchitectureMapping.toEntity(processorDto.getArchitecture()));
        }

        processorRepository.save(existingProcessor);
        return processorMapping.toDto(existingProcessor);
    }

    // DELETE
    @Transactional
    public void deleteProcessor(Long id) {
        if (!processorRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Processor with id '%d' not found", id)
            );
        }
        processorRepository.deleteById(id);
    }

    // Validation
    public void validateAddProcessor(ProcessorDto processorDto) {
        if (processorRepository.existsByNameIgnoreCase(processorDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Processor '%s' is already taken", processorDto.getName())
            );
        }
    }
}