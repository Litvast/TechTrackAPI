package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.litvast.techtrackapi.model.dto.RoomEquipmentDto;
import ru.litvast.techtrackapi.model.dto.RoomEquipmentUpdateDto;
import ru.litvast.techtrackapi.model.entity.RoomEquipment;

@Mapper(componentModel = "spring")
public interface RoomEquipmentMapping {

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "equipment.name", target = "equipmentName")
    @Mapping(source = "equipment.type", target = "equipmentType")
    RoomEquipmentDto toDto(RoomEquipment roomEquipment);

    RoomEquipment toEntity(RoomEquipmentDto dto);

    RoomEquipment toEntity(RoomEquipmentUpdateDto dto);
}