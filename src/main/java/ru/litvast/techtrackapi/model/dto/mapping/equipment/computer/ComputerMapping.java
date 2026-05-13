package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerDto;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ComputerUpdateDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;

@Mapper(componentModel = "spring")
public interface ComputerMapping {
    ComputerDto toDto(Computer computer);
    ComputerDto toDto(ComputerUpdateDto computerUpdateDto);
    ComputerUpdateDto toUpdateDto(Computer computer);
    Computer toEntity(ComputerDto computerDto);
    Computer toEntity(ComputerUpdateDto computerUpdateDto);
}
