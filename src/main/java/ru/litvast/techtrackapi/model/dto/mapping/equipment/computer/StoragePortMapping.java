package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.StoragePortDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.StoragePort;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoragePortMapping {
    StoragePortDto toDto(StoragePort entity);
    StoragePort toEntity(StoragePortDto dto);
    List<StoragePortDto> toDtoList(List<StoragePort> entities);
    List<StoragePort> toEntityList(List<StoragePortDto> dtos);
}