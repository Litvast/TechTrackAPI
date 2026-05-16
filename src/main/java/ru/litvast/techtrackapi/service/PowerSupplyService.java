package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.PowerSupplyDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.PowerSupplyMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PowerSupply;
import ru.litvast.techtrackapi.repository.equipment.computer.PowerSupplyRepository;
import ru.litvast.techtrackapi.util.Converter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PowerSupplyService {

    private final PowerSupplyRepository powerSupplyRepository;
    private final PowerSupplyMapping powerSupplyMapping;

    // CREATE
    @Transactional
    public PowerSupplyDto addPowerSupply(PowerSupplyDto powerSupplyDto) {
        log.info("=== НАЧАЛО: Добавление блока питания ===");
        log.info("Название: {}, Мощность: {} Вт", powerSupplyDto.getName(), powerSupplyDto.getPowerWatts());

        if (powerSupplyDto.getId() != null) {
            log.error("Передан ID при создании блока питания. ID: {}", powerSupplyDto.getId());
            throw new IllegalArgumentException("To create a power supply, you must specify a name, not an ID");
        }

        validateAddPowerSupply(powerSupplyDto);

        PowerSupply powerSupply = powerSupplyMapping.toEntity(powerSupplyDto);
        powerSupplyRepository.save(powerSupply);

        log.info("Блок питания создан. ID: {}", powerSupply.getId());
        log.info("=== УСПЕШНО: Блок питания добавлен ===");

        return powerSupplyMapping.toDto(powerSupply);
    }

    // READ all with pagination
    public Page<PowerSupplyDto> getAllPowerSupplies(Pageable pageable) {
        log.debug("Запрос всех блоков питания с пагинацией");

        Page<PowerSupply> powerSupplies = powerSupplyRepository.findAll(pageable);
        if (powerSupplies.isEmpty()) {
            log.warn("Блоки питания не найдены");
            throw new NoEntitiesFoundException("No power supplies found");
        }

        log.debug("Найдено {} блоков питания", powerSupplies.getTotalElements());
        return powerSupplies.map(powerSupplyMapping::toDto);
    }

    // READ by id
    public PowerSupplyDto getPowerSupplyById(Long id) {
        log.debug("Поиск блока питания по ID: {}", id);

        PowerSupply powerSupply = powerSupplyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Блок питания с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Power supply with id '%d' not found", id)
                    );
                });

        return powerSupplyMapping.toDto(powerSupply);
    }

    // READ by string id
    public PowerSupplyDto getPowerSupplyByStringId(String stringId) {
        log.debug("Поиск блока питания по строковому ID: {}", stringId);
        Long id = Converter.convertIdStringToLong(stringId);
        return getPowerSupplyById(id);
    }

    // READ by name
    public PowerSupplyDto getPowerSupplyByName(String name) {
        log.debug("Поиск блока питания по названию: {}", name);

        PowerSupply powerSupply = powerSupplyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Блок питания с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("Power supply with name '%s' not found", name)
                    );
                });

        return powerSupplyMapping.toDto(powerSupply);
    }

    // UPDATE
    @Transactional
    public PowerSupplyDto updatePowerSupply(Long id, PowerSupplyDto powerSupplyDto) {
        log.info("=== НАЧАЛО: Обновление блока питания ===");
        log.info("ID блока питания: {}", id);

        PowerSupply existingPowerSupply = powerSupplyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Блок питания с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Power supply with id '%d' not found", id)
                    );
                });

        if (!existingPowerSupply.getName().equalsIgnoreCase(powerSupplyDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingPowerSupply.getName(), powerSupplyDto.getName());

            if (powerSupplyRepository.existsByNameIgnoreCase(powerSupplyDto.getName())) {
                log.warn("Блок питания с названием '{}' уже существует", powerSupplyDto.getName());
                throw new IllegalArgumentException(
                        String.format("Power supply '%s' is already taken", powerSupplyDto.getName())
                );
            }
            existingPowerSupply.setName(powerSupplyDto.getName());
        }

        if (powerSupplyDto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingPowerSupply.getManufacturer(), powerSupplyDto.getManufacturer());
            existingPowerSupply.setManufacturer(powerSupplyDto.getManufacturer());
        }

        if (powerSupplyDto.getPowerWatts() != null) {
            log.info("Изменение мощности: {} -> {} Вт", existingPowerSupply.getPowerWatts(), powerSupplyDto.getPowerWatts());
            existingPowerSupply.setPowerWatts(powerSupplyDto.getPowerWatts());
        }

        if (powerSupplyDto.getEfficiency() != null) {
            log.info("Изменение эффективности: {} -> {}", existingPowerSupply.getEfficiency(), powerSupplyDto.getEfficiency());
            existingPowerSupply.setEfficiency(powerSupplyDto.getEfficiency());
        }

        if (powerSupplyDto.getFormFactor() != null) {
            log.info("Изменение форм-фактора: {} -> {}", existingPowerSupply.getFormFactor(), powerSupplyDto.getFormFactor());
            existingPowerSupply.setFormFactor(powerSupplyDto.getFormFactor());
        }

        if (powerSupplyDto.getModular() != null) {
            log.info("Изменение модульности: {} -> {}", existingPowerSupply.getModular(), powerSupplyDto.getModular());
            existingPowerSupply.setModular(powerSupplyDto.getModular());
        }

        powerSupplyRepository.save(existingPowerSupply);
        log.info("=== УСПЕШНО: Блок питания обновлён ===");

        return powerSupplyMapping.toDto(existingPowerSupply);
    }

    // DELETE
    @Transactional
    public void deletePowerSupply(Long id) {
        log.info("=== НАЧАЛО: Удаление блока питания ===");
        log.info("ID блока питания: {}", id);

        if (!powerSupplyRepository.existsById(id)) {
            log.error("Блок питания с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Power supply with id '%d' not found", id)
            );
        }

        powerSupplyRepository.deleteById(id);
        log.info("=== УСПЕШНО: Блок питания удалён ===");
    }

    // Validation
    public void validateAddPowerSupply(PowerSupplyDto powerSupplyDto) {
        if (powerSupplyRepository.existsByNameIgnoreCase(powerSupplyDto.getName())) {
            log.warn("Блок питания с названием '{}' уже существует", powerSupplyDto.getName());
            throw new IllegalArgumentException(
                    String.format("Power supply '%s' is already taken", powerSupplyDto.getName())
            );
        }
    }
}