package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ProcessorDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Processor;

@Mapper(componentModel = "spring")
public interface ProcessorMapping {
    ProcessorDto toDto(Processor processor);
    Processor toEntity(ProcessorDto processorDto);
}
