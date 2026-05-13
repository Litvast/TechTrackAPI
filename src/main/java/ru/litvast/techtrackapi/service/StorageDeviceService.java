package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
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

    public StorageDeviceDto addStorageDevice(StorageDeviceDto storageDeviceDto) {
        if (storageDeviceDto == null) {
            throw new IllegalArgumentException("Storage device cannot be null");
        }

        if (storageDeviceDto.getId() != null) {
            throw new IllegalArgumentException("To create a storage device, you must specify a name, not an ID");
        }

        validateAddStorageDevice(storageDeviceDto);

        StorageDevice storageDevice = storageDeviceRepository.save(storageDeviceMapping.toEntity(storageDeviceDto));
        return storageDeviceMapping.toDto(storageDevice);
    }

    public List<StorageDeviceDto> addSomeStorageDevices(List<StorageDeviceDto> storageDeviceDtoList) {
        if (storageDeviceDtoList == null || storageDeviceDtoList.isEmpty()) {
            throw new IllegalArgumentException("Storage device list cannot be empty");
        }

        storageDeviceDtoList.forEach(storageDeviceDto -> {
            if (storageDeviceDto.getId() != null) {
                System.out.println("To create a storage device, you must specify a name, not an ID");
            }

            validateAddStorageDevice(storageDeviceDto);
        });

        List<StorageDevice> storageDeviceList = storageDeviceMapping.toEntityList(storageDeviceDtoList);

        List<StorageDevice> storageDevices = storageDeviceRepository.saveAll(storageDeviceList);

        return storageDeviceMapping.toDtoList(storageDevices);
    }

    public StorageDeviceDto getStorageDeviceById(long id) {
        StorageDevice storageDevice = storageDeviceRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Storage device with id '%d' not found", id))
        );

        return storageDeviceMapping.toDto(storageDevice);
    }

    public void validateAddStorageDevice(StorageDeviceDto storageDeviceDto) {
        if (storageDeviceRepository.existsByNameIgnoreCase(storageDeviceDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Storage device '%s' is already taken", storageDeviceDto.getName())
            );
        }
    }

    public StorageDeviceDto getStorageDeviceByStringId(String stringId) {
        long id = Converter.convertIdStringToLong(stringId);

        return getStorageDeviceById(id);
    }
}
