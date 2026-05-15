package ru.litvast.techtrackapi.model.dto.mapping.equipment;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.RouterDto;
import ru.litvast.techtrackapi.model.dto.equipment.RouterUpdateDto;
import ru.litvast.techtrackapi.model.entity.equipment.Router;

@Mapper(componentModel = "spring")
public interface RouterMapping {
    RouterDto toDto(Router router);
    Router toEntity(RouterDto dto);
    Router toEntity(RouterUpdateDto dto);
}