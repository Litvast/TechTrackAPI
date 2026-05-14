package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.IoPortDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.IoPort;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IoPortMapping {
    IoPortDto toDto(IoPort entity);
    IoPort toEntity(IoPortDto dto);
    List<IoPortDto> toDtoList(List<IoPort> entities);
    List<IoPort> toEntityList(List<IoPortDto> dtos);
}