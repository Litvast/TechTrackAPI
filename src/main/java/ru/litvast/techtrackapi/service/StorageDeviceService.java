package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.StorageDeviceDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.StorageDeviceMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.StorageDevice;
import ru.litvast.techtrackapi.repository.equipment.computer.StorageDeviceRepository;
import ru.litvast.techtrackapi.util.Converter;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageDeviceService {

    private final StorageDeviceRepository storageDeviceRepository;
    private final StorageDeviceMapping storageDeviceMapping;

    // CREATE single
    @Transactional
    public StorageDeviceDto addStorageDevice(StorageDeviceDto storageDeviceDto) {
        log.info("=== НАЧАЛО: Добавление накопителя ===");
        log.info("Название: {}, Объём: {} ГБ",
                storageDeviceDto.getName(),
                storageDeviceDto.getCapacityGb());

        if (storageDeviceDto == null) {
            log.error("Накопитель не может быть null");
            throw new IllegalArgumentException("Storage device cannot be null");
        }

        if (storageDeviceDto.getId() != null) {
            log.error("Передан ID при создании накопителя. ID: {}", storageDeviceDto.getId());
            throw new IllegalArgumentException("To create a storage device, you must specify a name, not an ID");
        }

        validateAddStorageDevice(storageDeviceDto);

        StorageDevice storageDevice = storageDeviceMapping.toEntity(storageDeviceDto);
        storageDeviceRepository.save(storageDevice);

        log.info("Накопитель создан. ID: {}", storageDevice.getId());
        log.info("=== УСПЕШНО: Накопитель добавлен ===");

        return storageDeviceMapping.toDto(storageDevice);
    }

    // CREATE multiple
    @Transactional
    public List<StorageDeviceDto> addSomeStorageDevices(List<StorageDeviceDto> storageDeviceDtoList) {
        log.info("=== НАЧАЛО: Добавление нескольких накопителей ===");
        log.info("Количество накопителей: {}", storageDeviceDtoList.size());

        if (storageDeviceDtoList == null || storageDeviceDtoList.isEmpty()) {
            log.error("Список накопителей пуст");
            throw new IllegalArgumentException("Storage device list cannot be empty");
        }

        for (StorageDeviceDto dto : storageDeviceDtoList) {
            if (dto.getId() != null) {
                log.error("Передан ID при создании накопителя. ID: {}", dto.getId());
                throw new IllegalArgumentException("To create a storage device, you must specify a name, not an ID");
            }
            validateAddStorageDevice(dto);
        }

        List<StorageDevice> storageDeviceList = storageDeviceMapping.toEntityList(storageDeviceDtoList);
        List<StorageDevice> storageDevices = storageDeviceRepository.saveAll(storageDeviceList);

        log.info("Создано {} накопителей", storageDevices.size());
        log.info("=== УСПЕШНО: Накопители добавлены ===");

        return storageDeviceMapping.toDtoList(storageDevices);
    }

    // READ all with pagination
    public Page<StorageDeviceDto> getAllStorageDevices(Pageable pageable) {
        log.debug("Запрос всех накопителей с пагинацией");

        Page<StorageDevice> storageDevices = storageDeviceRepository.findAll(pageable);
        if (storageDevices.isEmpty()) {
            log.warn("Накопители не найдены");
            throw new NoEntitiesFoundException("No storage devices found");
        }

        log.debug("Найдено {} накопителей", storageDevices.getTotalElements());
        return storageDevices.map(storageDeviceMapping::toDto);
    }

    // READ by id
    public StorageDeviceDto getStorageDeviceById(Long id) {
        log.debug("Поиск накопителя по ID: {}", id);

        StorageDevice storageDevice = storageDeviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Накопитель с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Storage device with id '%d' not found", id)
                    );
                });

        return storageDeviceMapping.toDto(storageDevice);
    }

    // READ by string id
    public StorageDeviceDto getStorageDeviceByStringId(String stringId) {
        log.debug("Поиск накопителя по строковому ID: {}", stringId);
        Long id = Converter.convertIdStringToLong(stringId);
        return getStorageDeviceById(id);
    }

    // READ by name
    public StorageDeviceDto getStorageDeviceByName(String name) {
        log.debug("Поиск накопителя по названию: {}", name);

        StorageDevice storageDevice = storageDeviceRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Накопитель с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("Storage device with name '%s' not found", name)
                    );
                });

        return storageDeviceMapping.toDto(storageDevice);
    }

    // UPDATE
    @Transactional
    public StorageDeviceDto updateStorageDevice(Long id, StorageDeviceDto storageDeviceDto) {
        log.info("=== НАЧАЛО: Обновление накопителя ===");
        log.info("ID накопителя: {}", id);

        StorageDevice existingStorageDevice = storageDeviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Накопитель с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Storage device with id '%d' not found", id)
                    );
                });

        if (!existingStorageDevice.getName().equalsIgnoreCase(storageDeviceDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingStorageDevice.getName(), storageDeviceDto.getName());

            if (storageDeviceRepository.existsByNameIgnoreCase(storageDeviceDto.getName())) {
                log.warn("Накопитель с названием '{}' уже существует", storageDeviceDto.getName());
                throw new IllegalArgumentException(
                        String.format("Storage device '%s' is already taken", storageDeviceDto.getName())
                );
            }
            existingStorageDevice.setName(storageDeviceDto.getName());
        }

        if (storageDeviceDto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingStorageDevice.getManufacturer(), storageDeviceDto.getManufacturer());
            existingStorageDevice.setManufacturer(storageDeviceDto.getManufacturer());
        }

        if (storageDeviceDto.getCapacityGb() != null) {
            log.info("Изменение объёма: {} -> {} ГБ", existingStorageDevice.getCapacityGb(), storageDeviceDto.getCapacityGb());
            existingStorageDevice.setCapacityGb(storageDeviceDto.getCapacityGb());
        }

        if (storageDeviceDto.getPortType() != null) {
            log.info("Изменение типа порта: {} -> {}", existingStorageDevice.getPortType(), storageDeviceDto.getPortType());
            existingStorageDevice.setPortType(storageDeviceDto.getPortType());
        }

        if (storageDeviceDto.getConnectionInterface() != null) {
            log.info("Изменение интерфейса подключения: {} -> {}", existingStorageDevice.getConnectionInterface(), storageDeviceDto.getConnectionInterface());
            existingStorageDevice.setConnectionInterface(storageDeviceDto.getConnectionInterface());
        }

        if (storageDeviceDto.getFormFactor() != null) {
            log.info("Изменение форм-фактора: {} -> {}", existingStorageDevice.getFormFactor(), storageDeviceDto.getFormFactor());
            existingStorageDevice.setFormFactor(storageDeviceDto.getFormFactor());
        }

        if (storageDeviceDto.getReadSpeedMbps() != null) {
            log.info("Изменение скорости чтения: {} -> {} МБ/с", existingStorageDevice.getReadSpeedMbps(), storageDeviceDto.getReadSpeedMbps());
            existingStorageDevice.setReadSpeedMbps(storageDeviceDto.getReadSpeedMbps());
        }

        if (storageDeviceDto.getWriteSpeedMbps() != null) {
            log.info("Изменение скорости записи: {} -> {} МБ/с", existingStorageDevice.getWriteSpeedMbps(), storageDeviceDto.getWriteSpeedMbps());
            existingStorageDevice.setWriteSpeedMbps(storageDeviceDto.getWriteSpeedMbps());
        }

        if (storageDeviceDto.getNandType() != null) {
            log.info("Изменение типа NAND: {} -> {}", existingStorageDevice.getNandType(), storageDeviceDto.getNandType());
            existingStorageDevice.setNandType(storageDeviceDto.getNandType());
        }

        if (storageDeviceDto.getTbw() != null) {
            log.info("Изменение TBW: {} -> {} ТБ", existingStorageDevice.getTbw(), storageDeviceDto.getTbw());
            existingStorageDevice.setTbw(storageDeviceDto.getTbw());
        }

        if (storageDeviceDto.getRpm() != null) {
            log.info("Изменение RPM: {} -> {} об/мин", existingStorageDevice.getRpm(), storageDeviceDto.getRpm());
            existingStorageDevice.setRpm(storageDeviceDto.getRpm());
        }

        if (storageDeviceDto.getHeightMm() != null) {
            log.info("Изменение высоты: {} -> {} мм", existingStorageDevice.getHeightMm(), storageDeviceDto.getHeightMm());
            existingStorageDevice.setHeightMm(storageDeviceDto.getHeightMm());
        }

        storageDeviceRepository.save(existingStorageDevice);
        log.info("=== УСПЕШНО: Накопитель обновлён ===");

        return storageDeviceMapping.toDto(existingStorageDevice);
    }

    // DELETE
    @Transactional
    public void deleteStorageDevice(Long id) {
        log.info("=== НАЧАЛО: Удаление накопителя ===");
        log.info("ID накопителя: {}", id);

        if (!storageDeviceRepository.existsById(id)) {
            log.error("Накопитель с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Storage device with id '%d' not found", id)
            );
        }

        storageDeviceRepository.deleteById(id);
        log.info("=== УСПЕШНО: Накопитель удалён ===");
    }

    // Validation
    public void validateAddStorageDevice(StorageDeviceDto storageDeviceDto) {
        if (storageDeviceRepository.existsByNameIgnoreCase(storageDeviceDto.getName())) {
            log.warn("Накопитель с названием '{}' уже существует", storageDeviceDto.getName());
            throw new IllegalArgumentException(
                    String.format("Storage device '%s' is already taken", storageDeviceDto.getName())
            );
        }
    }
}