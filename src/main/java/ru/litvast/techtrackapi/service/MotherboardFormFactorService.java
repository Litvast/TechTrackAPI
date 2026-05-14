package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.MotherboardFormFactorDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.MotherboardFormFactorMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.MotherboardFormFactor;
import ru.litvast.techtrackapi.repository.equipment.computer.MotherboardFormFactorRepository;

@Service
@RequiredArgsConstructor
public class MotherboardFormFactorService {

    private final MotherboardFormFactorRepository formFactorRepository;
    private final MotherboardFormFactorMapping formFactorMapping;

    @Transactional
    public MotherboardFormFactorDto addFormFactor(MotherboardFormFactorDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a form factor, you must specify name and code, not an ID");
        }

        if (formFactorRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new IllegalArgumentException("Form factor with code '" + dto.getCode() + "' already exists");
        }

        MotherboardFormFactor entity = formFactorMapping.toEntity(dto);
        formFactorRepository.save(entity);
        return formFactorMapping.toDto(entity);
    }

    public MotherboardFormFactorDto getFormFactorById(Long id) {
        MotherboardFormFactor entity = formFactorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Form factor with id '" + id + "' not found"));
        return formFactorMapping.toDto(entity);
    }

    public MotherboardFormFactorDto getFormFactorByCode(String code) {
        MotherboardFormFactor entity = formFactorRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new EntityNotFoundException("Form factor with code '" + code + "' not found"));
        return formFactorMapping.toDto(entity);
    }

    public Page<MotherboardFormFactorDto> getAllFormFactors(Pageable pageable) {
        Page<MotherboardFormFactor> entities = formFactorRepository.findAll(pageable);
        if (entities.isEmpty()) throw new NoEntitiesFoundException("No form factors found");
        return entities.map(formFactorMapping::toDto);
    }

    @Transactional
    public MotherboardFormFactorDto updateFormFactor(Long id, MotherboardFormFactorDto dto) {
        MotherboardFormFactor existing = formFactorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Form factor with id '" + id + "' not found"));

        if (!existing.getCode().equalsIgnoreCase(dto.getCode())) {
            if (formFactorRepository.existsByCodeIgnoreCase(dto.getCode())) {
                throw new IllegalArgumentException("Form factor with code '" + dto.getCode() + "' already exists");
            }
        }

        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setWidthMm(dto.getWidthMm());
        existing.setHeightMm(dto.getHeightMm());

        formFactorRepository.save(existing);
        return formFactorMapping.toDto(existing);
    }

    @Transactional
    public void deleteFormFactor(Long id) {
        if (!formFactorRepository.existsById(id)) {
            throw new EntityNotFoundException("Form factor with id '" + id + "' not found");
        }
        formFactorRepository.deleteById(id);
    }
}