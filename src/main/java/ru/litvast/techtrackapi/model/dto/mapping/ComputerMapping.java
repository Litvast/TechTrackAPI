package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;

@Mapper(componentModel = "spring")
public interface ComputerMapping {
    ComputerDto toDto(Computer computer);
    Computer toEntity(ComputerDto computerDto);
}
