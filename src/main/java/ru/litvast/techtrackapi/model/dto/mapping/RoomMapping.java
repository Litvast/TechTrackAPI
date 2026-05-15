package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.litvast.techtrackapi.model.dto.RoomDto;
import ru.litvast.techtrackapi.model.dto.RoomUpdateDto;
import ru.litvast.techtrackapi.model.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapping {

    @Mapping(source = "buildingFloor.id", target = "buildingFloorId")
    RoomDto toDto(Room room);

    Room toEntity(RoomDto dto);
    Room toEntity(RoomUpdateDto dto);
}