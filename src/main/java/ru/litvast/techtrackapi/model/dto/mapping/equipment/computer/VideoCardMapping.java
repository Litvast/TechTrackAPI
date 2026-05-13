package ru.litvast.techtrackapi.model.dto.mapping.equipment.computer;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.equipment.computer.VideoCardDto;
import ru.litvast.techtrackapi.model.entity.equipment.computer.VideoCard;

@Mapper(componentModel = "spring")
public interface VideoCardMapping {
    VideoCardDto toDto(VideoCard videoCard);
    VideoCard toEntity(VideoCardDto videoCardDto);
}
