package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.VideoCardDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.VideoCardMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.VideoCard;
import ru.litvast.techtrackapi.repository.equipment.computer.VideoCardRepository;
import ru.litvast.techtrackapi.util.Converter;

@Service
@RequiredArgsConstructor
public class VideoCardService {

    private final VideoCardRepository videoCardRepository;
    private final VideoCardMapping videoCardMapping;

    public VideoCardDto addVideoCard(VideoCardDto videoCardDto) {
        if (videoCardDto.getId() != null) {
            throw new IllegalArgumentException("To create a video card, you must specify a name, not an ID");
        }

        validateAddVideoCard(videoCardDto);

        VideoCard videoCard = videoCardRepository.save(videoCardMapping.toEntity(videoCardDto));
        return videoCardMapping.toDto(videoCard);
    }

    public VideoCardDto getVideoCardById(long id) {
        VideoCard videoCard = videoCardRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Video card with id '%d' not found", id))
        );

        return videoCardMapping.toDto(videoCard);
    }

    public void validateAddVideoCard(VideoCardDto videoCardDto) {
        if (videoCardRepository.existsByNameIgnoreCase(videoCardDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Video card '%s' is already taken", videoCardDto.getName())
            );
        }
    }

    public VideoCardDto getVideoCardByStringId(String stringId) {
        long id = Converter.convertIdStringToLong(stringId);

        return getVideoCardById(id);
    }
}
