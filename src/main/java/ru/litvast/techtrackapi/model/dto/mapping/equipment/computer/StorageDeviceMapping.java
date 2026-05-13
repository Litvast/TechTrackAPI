package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.StorageDeviceDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.StorageDevice;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StorageDeviceMapping {
    StorageDeviceDto toDto(StorageDevice storageDevice);
    StorageDevice toEntity(StorageDeviceDto storageDeviceDto);
    List<StorageDeviceDto> toDtoList(List<StorageDevice> storageDeviceList);
    List<StorageDevice> toEntityList(List<StorageDeviceDto> storageDeviceDtoList);
}
