package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class StorageDeviceService {

    private final StorageDeviceRepository storageDeviceRepository;
    private final StorageDeviceMapping storageDeviceMapping;

    // CREATE single
    @Transactional
    public StorageDeviceDto addStorageDevice(StorageDeviceDto storageDeviceDto) {
        if (storageDeviceDto == null) {
            throw new IllegalArgumentException("Storage device cannot be null");
        }

        if (storageDeviceDto.getId() != null) {
            throw new IllegalArgumentException("To create a storage device, you must specify a name, not an ID");
        }

        validateAddStorageDevice(storageDeviceDto);

        StorageDevice storageDevice = storageDeviceMapping.toEntity(storageDeviceDto);
        storageDeviceRepository.save(storageDevice);
        return storageDeviceMapping.toDto(storageDevice);
    }

    // CREATE multiple
    @Transactional
    public List<StorageDeviceDto> addSomeStorageDevices(List<StorageDeviceDto> storageDeviceDtoList) {
        if (storageDeviceDtoList == null || storageDeviceDtoList.isEmpty()) {
            throw new IllegalArgumentException("Storage device list cannot be empty");
        }

        storageDeviceDtoList.forEach(storageDeviceDto -> {
            if (storageDeviceDto.getId() != null) {
                throw new IllegalArgumentException("To create a storage device, you must specify a name, not an ID");
            }
            validateAddStorageDevice(storageDeviceDto);
        });

        List<StorageDevice> storageDeviceList = storageDeviceMapping.toEntityList(storageDeviceDtoList);
        List<StorageDevice> storageDevices = storageDeviceRepository.saveAll(storageDeviceList);
        return storageDeviceMapping.toDtoList(storageDevices);
    }

    // READ all with pagination
    public Page<StorageDeviceDto> getAllStorageDevices(Pageable pageable) {
        Page<StorageDevice> storageDevices = storageDeviceRepository.findAll(pageable);
        if (storageDevices.isEmpty()) {
            throw new NoEntitiesFoundException("No storage devices found");
        }
        return storageDevices.map(storageDeviceMapping::toDto);
    }

    // READ by id
    public StorageDeviceDto getStorageDeviceById(Long id) {
        StorageDevice storageDevice = storageDeviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Storage device with id '%d' not found", id)
                ));
        return storageDeviceMapping.toDto(storageDevice);
    }

    // READ by string id
    public StorageDeviceDto getStorageDeviceByStringId(String stringId) {
        Long id = Converter.convertIdStringToLong(stringId);
        return getStorageDeviceById(id);
    }

    // READ by name
    public StorageDeviceDto getStorageDeviceByName(String name) {
        StorageDevice storageDevice = storageDeviceRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Storage device with name '%s' not found", name)
                ));
        return storageDeviceMapping.toDto(storageDevice);
    }

    // UPDATE
    @Transactional
    public StorageDeviceDto updateStorageDevice(Long id, StorageDeviceDto storageDeviceDto) {
        StorageDevice existingStorageDevice = storageDeviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Storage device with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingStorageDevice.getName().equalsIgnoreCase(storageDeviceDto.getName())) {
            if (storageDeviceRepository.existsByNameIgnoreCase(storageDeviceDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Storage device '%s' is already taken", storageDeviceDto.getName())
                );
            }
        }

        existingStorageDevice.setName(storageDeviceDto.getName());
        existingStorageDevice.setManufacturer(storageDeviceDto.getManufacturer());
        existingStorageDevice.setCapacityGb(storageDeviceDto.getCapacityGb());
        existingStorageDevice.setPortType(storageDeviceDto.getPortType());
        existingStorageDevice.setConnectionInterface(storageDeviceDto.getConnectionInterface());
        existingStorageDevice.setFormFactor(storageDeviceDto.getFormFactor());
        existingStorageDevice.setReadSpeedMbps(storageDeviceDto.getReadSpeedMbps());
        existingStorageDevice.setWriteSpeedMbps(storageDeviceDto.getWriteSpeedMbps());
        existingStorageDevice.setNandType(storageDeviceDto.getNandType());
        existingStorageDevice.setTbw(storageDeviceDto.getTbw());
        existingStorageDevice.setRpm(storageDeviceDto.getRpm());
        existingStorageDevice.setHeightMm(storageDeviceDto.getHeightMm());

        storageDeviceRepository.save(existingStorageDevice);
        return storageDeviceMapping.toDto(existingStorageDevice);
    }

    // DELETE
    @Transactional
    public void deleteStorageDevice(Long id) {
        if (!storageDeviceRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Storage device with id '%d' not found", id)
            );
        }
        storageDeviceRepository.deleteById(id);
    }

    // Validation
    public void validateAddStorageDevice(StorageDeviceDto storageDeviceDto) {
        if (storageDeviceRepository.existsByNameIgnoreCase(storageDeviceDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Storage device '%s' is already taken", storageDeviceDto.getName())
            );
        }
    }
}