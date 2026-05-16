package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.RamDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.RamMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;
import ru.litvast.techtrackapi.repository.equipment.computer.RamRepository;
import ru.litvast.techtrackapi.util.Converter;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RamService {

    private final RamRepository ramRepository;
    private final RamMapping ramMapping;

    // CREATE single
    @Transactional
    public RamDto addRam(RamDto ramDto) {
        log.info("=== НАЧАЛО: Добавление модуля оперативной памяти ===");
        log.info("Название: {}, Объём: {} МБ, Частота: {} МГц",
                ramDto.getName(), ramDto.getCapacityMb(), ramDto.getFrequencyMHz());

        if (ramDto == null) {
            log.error("RAM не может быть null");
            throw new IllegalArgumentException("RAM cannot be null");
        }

        if (ramDto.getId() != null) {
            log.error("Передан ID при создании RAM. ID: {}", ramDto.getId());
            throw new IllegalArgumentException("To create a RAM, you must specify a name, not an ID");
        }

        validateAddRam(ramDto);

        Ram ram = ramMapping.toEntity(ramDto);
        ramRepository.save(ram);

        log.info("Модуль RAM создан. ID: {}", ram.getId());
        log.info("=== УСПЕШНО: Модуль RAM добавлен ===");

        return ramMapping.toDto(ram);
    }

    // CREATE multiple
    @Transactional
    public List<RamDto> addSomeRam(List<RamDto> ramDtoList) {
        log.info("=== НАЧАЛО: Добавление нескольких модулей RAM ===");
        log.info("Количество модулей: {}", ramDtoList.size());

        if (ramDtoList == null || ramDtoList.isEmpty()) {
            log.error("Список RAM пуст");
            throw new IllegalArgumentException("RAM list cannot be empty");
        }

        for (RamDto ramDto : ramDtoList) {
            if (ramDto.getId() != null) {
                log.error("Передан ID при создании RAM. ID: {}", ramDto.getId());
                throw new IllegalArgumentException("To create a RAM, you must specify a name, not an ID");
            }
            validateAddRam(ramDto);
        }

        List<Ram> ramList = ramMapping.toEntityList(ramDtoList);
        List<Ram> rams = ramRepository.saveAll(ramList);

        log.info("Создано {} модулей RAM", rams.size());
        log.info("=== УСПЕШНО: Модули RAM добавлены ===");

        return ramMapping.toDtoList(rams);
    }

    // READ all with pagination
    public Page<RamDto> getAllRams(Pageable pageable) {
        log.debug("Запрос всех модулей RAM с пагинацией");

        Page<Ram> rams = ramRepository.findAll(pageable);
        if (rams.isEmpty()) {
            log.warn("Модули RAM не найдены");
            throw new NoEntitiesFoundException("No RAMs found");
        }

        log.debug("Найдено {} модулей RAM", rams.getTotalElements());
        return rams.map(ramMapping::toDto);
    }

    // READ by id
    public RamDto getRamById(Long id) {
        log.debug("Поиск модуля RAM по ID: {}", id);

        Ram ram = ramRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Модуль RAM с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("RAM with id '%d' not found", id)
                    );
                });

        return ramMapping.toDto(ram);
    }

    // READ by string id
    public RamDto getRamByStringId(String stringId) {
        log.debug("Поиск модуля RAM по строковому ID: {}", stringId);
        Long id = Converter.convertIdStringToLong(stringId);
        return getRamById(id);
    }

    // READ by name
    public RamDto getRamByName(String name) {
        log.debug("Поиск модуля RAM по названию: {}", name);

        Ram ram = ramRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Модуль RAM с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("RAM with name '%s' not found", name)
                    );
                });

        return ramMapping.toDto(ram);
    }

    // UPDATE
    @Transactional
    public RamDto updateRam(Long id, RamDto ramDto) {
        log.info("=== НАЧАЛО: Обновление модуля RAM ===");
        log.info("ID RAM: {}", id);

        Ram existingRam = ramRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Модуль RAM с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("RAM with id '%d' not found", id)
                    );
                });

        if (!existingRam.getName().equalsIgnoreCase(ramDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingRam.getName(), ramDto.getName());

            if (ramRepository.existsByNameIgnoreCase(ramDto.getName())) {
                log.warn("Модуль RAM с названием '{}' уже существует", ramDto.getName());
                throw new IllegalArgumentException(
                        String.format("RAM '%s' is already taken", ramDto.getName())
                );
            }
            existingRam.setName(ramDto.getName());
        }

        if (ramDto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingRam.getManufacturer(), ramDto.getManufacturer());
            existingRam.setManufacturer(ramDto.getManufacturer());
        }

        if (ramDto.getType() != null) {
            log.info("Изменение типа: {} -> {}", existingRam.getType(), ramDto.getType());
            existingRam.setType(ramDto.getType());
        }

        if (ramDto.getFormFactor() != null) {
            log.info("Изменение форм-фактора: {} -> {}", existingRam.getFormFactor(), ramDto.getFormFactor());
            existingRam.setFormFactor(ramDto.getFormFactor());
        }

        if (ramDto.getCapacityMb() != null) {
            log.info("Изменение объёма: {} -> {} МБ", existingRam.getCapacityMb(), ramDto.getCapacityMb());
            existingRam.setCapacityMb(ramDto.getCapacityMb());
        }

        if (ramDto.getFrequencyMHz() != null) {
            log.info("Изменение частоты: {} -> {} МГц", existingRam.getFrequencyMHz(), ramDto.getFrequencyMHz());
            existingRam.setFrequencyMHz(ramDto.getFrequencyMHz());
        }

        if (ramDto.getTimings() != null) {
            log.info("Изменение таймингов: {} -> {}", existingRam.getTimings(), ramDto.getTimings());
            existingRam.setTimings(ramDto.getTimings());
        }

        if (ramDto.getVoltage() != null) {
            log.info("Изменение напряжения: {} -> {} В", existingRam.getVoltage(), ramDto.getVoltage());
            existingRam.setVoltage(ramDto.getVoltage());
        }

        if (ramDto.getEcc() != null) {
            log.info("Изменение ECC: {} -> {}", existingRam.getEcc(), ramDto.getEcc());
            existingRam.setEcc(ramDto.getEcc());
        }

        if (ramDto.getRegistered() != null) {
            log.info("Изменение Registered: {} -> {}", existingRam.getRegistered(), ramDto.getRegistered());
            existingRam.setRegistered(ramDto.getRegistered());
        }

        if (ramDto.getXmpSupport() != null) {
            log.info("Изменение поддержки XMP: {} -> {}", existingRam.getXmpSupport(), ramDto.getXmpSupport());
            existingRam.setXmpSupport(ramDto.getXmpSupport());
        }

        if (ramDto.getExpoSupport() != null) {
            log.info("Изменение поддержки EXPO: {} -> {}", existingRam.getExpoSupport(), ramDto.getExpoSupport());
            existingRam.setExpoSupport(ramDto.getExpoSupport());
        }

        if (ramDto.getDualRank() != null) {
            log.info("Изменение Dual Rank: {} -> {}", existingRam.getDualRank(), ramDto.getDualRank());
            existingRam.setDualRank(ramDto.getDualRank());
        }

        if (ramDto.getOnDieEcc() != null) {
            log.info("Изменение On-Die ECC: {} -> {}", existingRam.getOnDieEcc(), ramDto.getOnDieEcc());
            existingRam.setOnDieEcc(ramDto.getOnDieEcc());
        }

        ramRepository.save(existingRam);
        log.info("=== УСПЕШНО: Модуль RAM обновлён ===");

        return ramMapping.toDto(existingRam);
    }

    // DELETE
    @Transactional
    public void deleteRam(Long id) {
        log.info("=== НАЧАЛО: Удаление модуля RAM ===");
        log.info("ID RAM: {}", id);

        if (!ramRepository.existsById(id)) {
            log.error("Модуль RAM с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("RAM with id '%d' not found", id)
            );
        }

        ramRepository.deleteById(id);
        log.info("=== УСПЕШНО: Модуль RAM удалён ===");
    }

    // Validation
    public void validateAddRam(RamDto ramDto) {
        if (ramRepository.existsByNameIgnoreCase(ramDto.getName())) {
            log.warn("Модуль RAM с названием '{}' уже существует", ramDto.getName());
            throw new IllegalArgumentException(
                    String.format("RAM '%s' is already taken", ramDto.getName())
            );
        }
    }
}