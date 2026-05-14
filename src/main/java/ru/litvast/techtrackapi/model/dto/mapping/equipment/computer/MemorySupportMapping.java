package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.MemorySupportDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.MemorySupport;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemorySupportMapping {
    MemorySupportDto toDto(MemorySupport entity);
    MemorySupport toEntity(MemorySupportDto dto);
    List<MemorySupportDto> toDtoList(List<MemorySupport> entities);
    List<MemorySupport> toEntityList(List<MemorySupportDto> dtos);
}