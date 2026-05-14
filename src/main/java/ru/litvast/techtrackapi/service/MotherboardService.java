package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a motherboard, you must specify a name, not an ID");
        }

        // Сохраняем formFactor, если новый
        if (dto.getFormFactor() != null && dto.getFormFactor().getId() == null) {
            MotherboardFormFactorDto saved = formFactorService.addFormFactor(dto.getFormFactor());
            dto.setFormFactor(saved);
        }

        // Сохраняем socket, если новый
        if (dto.getSocket() != null && dto.getSocket().getId() == null) {
            CpuSocketDto saved = cpuSocketService.addCpuSocket(dto.getSocket());
            dto.setSocket(saved);
        }

        validateAddMotherboard(dto);

        Motherboard entity = motherboardMapping.toEntity(dto);
        motherboardRepository.save(entity);
        return motherboardMapping.toDto(entity);
    }

    // READ all with pagination
    public Page<MotherboardDto> getAllMotherboards(Pageable pageable) {
        Page<Motherboard> entities = motherboardRepository.findAll(pageable);
        if (entities.isEmpty()) {
            throw new NoEntitiesFoundException("No motherboards found");
        }
        return entities.map(motherboardMapping::toDto);
    }

    // READ by id
    public MotherboardDto getMotherboardById(Long id) {
        Motherboard entity = motherboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Motherboard with id '%d' not found", id)
                ));
        return motherboardMapping.toDto(entity);
    }

    // READ by name
    public MotherboardDto getMotherboardByName(String name) {
        Motherboard entity = motherboardRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Motherboard with name '%s' not found", name)
                ));
        return motherboardMapping.toDto(entity);
    }

    // UPDATE
    @Transactional
    public MotherboardDto updateMotherboard(Long id, MotherboardDto dto) {
        Motherboard existing = motherboardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Motherboard with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existing.getName().equalsIgnoreCase(dto.getName())) {
            if (motherboardRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Motherboard '%s' is already taken", dto.getName())
                );
            }
            existing.setName(dto.getName());
        }

        // Обновляем formFactor
        if (dto.getFormFactor() != null) {
            if (dto.getFormFactor().getId() != null) {
                existing.setFormFactor(motherboardFormFactorMapping.toEntity(formFactorService.getFormFactorById(dto.getFormFactor().getId())));
            } else {
                MotherboardFormFactorDto saved = formFactorService.addFormFactor(dto.getFormFactor());
                existing.setFormFactor(motherboardFormFactorMapping.toEntity(formFactorService.getFormFactorById(saved.getId())));
            }
        }

        // Обновляем socket
        if (dto.getSocket() != null) {
            if (dto.getSocket().getId() != null) {
                existing.setSocket(cpuSocketMapping.toEntity(cpuSocketService.getCpuSocketById(dto.getSocket().getId())));
            } else {
                CpuSocketDto saved = cpuSocketService.addCpuSocket(dto.getSocket());
                existing.setSocket(cpuSocketMapping.toEntity(cpuSocketService.getCpuSocketById(saved.getId())));
            }
        }

        // Обновляем простые поля
        existing.setManufacturer(dto.getManufacturer());
        existing.setChipset(dto.getChipset());
        existing.setMemorySupports(memorySupportMapping.toEntityList(dto.getMemorySupports()));
        existing.setStoragePorts(storagePortMapping.toEntityList(dto.getStoragePorts()));
        existing.setIoPorts(ioPortMapping.toEntityList(dto.getIoPorts()));

        motherboardRepository.save(existing);
        return motherboardMapping.toDto(existing);
    }

    // DELETE
    @Transactional
    public void deleteMotherboard(Long id) {
        if (!motherboardRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Motherboard with id '%d' not found", id)
            );
        }
        motherboardRepository.deleteById(id);
    }

    // Validation
    public void validateAddMotherboard(MotherboardDto dto) {
        if (motherboardRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Motherboard '%s' is already taken", dto.getName())
            );
        }
    }
}