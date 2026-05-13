package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.RamDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RamMapping {
    RamDto toDto(Ram ram);
    Ram toEntity(RamDto ramDto);
    List<RamDto> toDtoList(List<Ram> ramList);
    List<Ram> toEntityList(List<RamDto> ramDtoList);
}
