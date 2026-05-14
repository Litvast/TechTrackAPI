package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class CpuArchitectureService {

    private final CpuArchitectureRepository architectureRepository;
    private final CpuArchitectureMapping architectureMapping;

    private final CpuSocketRepository cpuSocketRepository;

    // CREATE
    @Transactional
    public CpuArchitectureDto addCpuArchitecture(CpuArchitectureDto architectureDto) {
        if (architectureDto.getId() != null) {
            throw new IllegalArgumentException("To create an architecture, you must specify a name, not an ID");
        }

        validateAddCpuArchitecture(architectureDto);

        CpuArchitecture architecture = architectureMapping.toEntity(architectureDto);
        architectureRepository.save(architecture);
        return architectureMapping.toDto(architecture);
    }

    // READ all with pagination
    public Page<CpuArchitectureDto> getAllCpuArchitectures(Pageable pageable) {
        Page<CpuArchitecture> architectures = architectureRepository.findAll(pageable);
        if (architectures.isEmpty()) {
            throw new NoEntitiesFoundException("No CPU architectures found");
        }
        return architectures.map(architectureMapping::toDto);
    }

    // READ by id
    public CpuArchitectureDto getCpuArchitectureById(Long id) {
        CpuArchitecture architecture = architectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CpuArchitecture with id '%d' not found", id)
                ));
        return architectureMapping.toDto(architecture);
    }

    // READ by string id
    public CpuArchitectureDto getCpuArchitectureByStringId(String stringId) {
        Long id = Converter.convertIdStringToLong(stringId);
        return getCpuArchitectureById(id);
    }

    // READ by name
    public CpuArchitectureDto getCpuArchitectureByName(String name) {
        CpuArchitecture architecture = architectureRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CpuArchitecture with name '%s' not found", name)
                ));
        return architectureMapping.toDto(architecture);
    }

    // UPDATE
    @Transactional
    public CpuArchitectureDto updateCpuArchitecture(Long id, CpuArchitectureDto architectureDto) {
        CpuArchitecture existingArchitecture = architectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CpuArchitecture with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingArchitecture.getName().equalsIgnoreCase(architectureDto.getName())) {
            if (architectureRepository.existsByNameIgnoreCase(architectureDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("CpuArchitecture '%s' is already taken", architectureDto.getName())
                );
            }
        }

        existingArchitecture.setName(architectureDto.getName());
        existingArchitecture.setBitWidth(architectureDto.getBitWidth());

        architectureRepository.save(existingArchitecture);
        return architectureMapping.toDto(existingArchitecture);
    }

    // DELETE
    @Transactional
    public void deleteCpuArchitecture(Long id) {
        if (!architectureRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("CpuArchitecture with id '%d' not found", id)
            );
        }
        architectureRepository.deleteById(id);
    }

    // Validation
    public void validateAddCpuArchitecture(CpuArchitectureDto architectureDto) {
        if (architectureRepository.existsByNameIgnoreCase(architectureDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("CpuArchitecture '%s' is already taken", architectureDto.getName())
            );
        }
    }
}