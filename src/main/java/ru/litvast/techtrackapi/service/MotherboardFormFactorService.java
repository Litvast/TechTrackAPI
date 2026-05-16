package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MotherboardFormFactorService {

    private final MotherboardFormFactorRepository formFactorRepository;
    private final MotherboardFormFactorMapping formFactorMapping;

    @Transactional
    public MotherboardFormFactorDto addFormFactor(MotherboardFormFactorDto dto) {
        log.info("=== НАЧАЛО: Добавление форм-фактора материнской платы ===");
        log.info("Код: {}, Название: {}", dto.getCode(), dto.getName());

        if (dto.getId() != null) {
            log.error("Передан ID при создании форм-фактора. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a form factor, you must specify name and code, not an ID");
        }

        if (formFactorRepository.existsByCodeIgnoreCase(dto.getCode())) {
            log.warn("Форм-фактор с кодом '{}' уже существует", dto.getCode());
            throw new IllegalArgumentException("Form factor with code '" + dto.getCode() + "' already exists");
        }

        MotherboardFormFactor entity = formFactorMapping.toEntity(dto);
        formFactorRepository.save(entity);

        log.info("Форм-фактор создан. ID: {}", entity.getId());
        log.info("=== УСПЕШНО: Форм-фактор добавлен ===");

        return formFactorMapping.toDto(entity);
    }

    public MotherboardFormFactorDto getFormFactorById(Long id) {
        log.debug("Поиск форм-фактора по ID: {}", id);

        MotherboardFormFactor entity = formFactorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Форм-фактор с ID {} не найден", id);
                    return new EntityNotFoundException("Form factor with id '" + id + "' not found");
                });

        return formFactorMapping.toDto(entity);
    }

    public MotherboardFormFactorDto getFormFactorByCode(String code) {
        log.debug("Поиск форм-фактора по коду: {}", code);

        MotherboardFormFactor entity = formFactorRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> {
                    log.error("Форм-фактор с кодом '{}' не найден", code);
                    return new EntityNotFoundException("Form factor with code '" + code + "' not found");
                });

        return formFactorMapping.toDto(entity);
    }

    public Page<MotherboardFormFactorDto> getAllFormFactors(Pageable pageable) {
        log.debug("Запрос всех форм-факторов с пагинацией");

        Page<MotherboardFormFactor> entities = formFactorRepository.findAll(pageable);
        if (entities.isEmpty()) {
            log.warn("Форм-факторы не найдены");
            throw new NoEntitiesFoundException("No form factors found");
        }

        log.debug("Найдено {} форм-факторов", entities.getTotalElements());
        return entities.map(formFactorMapping::toDto);
    }

    @Transactional
    public MotherboardFormFactorDto updateFormFactor(Long id, MotherboardFormFactorDto dto) {
        log.info("=== НАЧАЛО: Обновление форм-фактора материнской платы ===");
        log.info("ID форм-фактора: {}", id);

        MotherboardFormFactor existing = formFactorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Форм-фактор с ID {} не найден", id);
                    return new EntityNotFoundException("Form factor with id '" + id + "' not found");
                });

        if (!existing.getCode().equalsIgnoreCase(dto.getCode())) {
            log.info("Изменение кода: {} -> {}", existing.getCode(), dto.getCode());

            if (formFactorRepository.existsByCodeIgnoreCase(dto.getCode())) {
                log.warn("Форм-фактор с кодом '{}' уже существует", dto.getCode());
                throw new IllegalArgumentException("Form factor with code '" + dto.getCode() + "' already exists");
            }
            existing.setCode(dto.getCode());
        }

        if (dto.getName() != null) {
            log.info("Изменение названия: {} -> {}", existing.getName(), dto.getName());
            existing.setName(dto.getName());
        }

        if (dto.getWidthMm() != null) {
            log.info("Изменение ширины: {} -> {} мм", existing.getWidthMm(), dto.getWidthMm());
            existing.setWidthMm(dto.getWidthMm());
        }

        if (dto.getHeightMm() != null) {
            log.info("Изменение высоты: {} -> {} мм", existing.getHeightMm(), dto.getHeightMm());
            existing.setHeightMm(dto.getHeightMm());
        }

        formFactorRepository.save(existing);
        log.info("=== УСПЕШНО: Форм-фактор обновлён ===");

        return formFactorMapping.toDto(existing);
    }

    @Transactional
    public void deleteFormFactor(Long id) {
        log.info("=== НАЧАЛО: Удаление форм-фактора материнской платы ===");
        log.info("ID форм-фактора: {}", id);

        if (!formFactorRepository.existsById(id)) {
            log.error("Форм-фактор с ID {} не найден", id);
            throw new EntityNotFoundException("Form factor with id '" + id + "' not found");
        }

        formFactorRepository.deleteById(id);
        log.info("=== УСПЕШНО: Форм-фактор удалён ===");
    }
}