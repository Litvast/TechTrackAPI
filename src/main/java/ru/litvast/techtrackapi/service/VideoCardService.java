package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
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

    // CREATE
    @Transactional
    public VideoCardDto addVideoCard(VideoCardDto videoCardDto) {
        if (videoCardDto.getId() != null) {
            throw new IllegalArgumentException("To create a video card, you must specify a name, not an ID");
        }

        validateAddVideoCard(videoCardDto);

        VideoCard videoCard = videoCardMapping.toEntity(videoCardDto);
        videoCardRepository.save(videoCard);
        return videoCardMapping.toDto(videoCard);
    }

    // READ all with pagination
    public Page<VideoCardDto> getAllVideoCards(Pageable pageable) {
        Page<VideoCard> videoCards = videoCardRepository.findAll(pageable);
        if (videoCards.isEmpty()) {
            throw new NoEntitiesFoundException("No video cards found");
        }
        return videoCards.map(videoCardMapping::toDto);
    }

    // READ by id
    public VideoCardDto getVideoCardById(Long id) {
        VideoCard videoCard = videoCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Video card with id '%d' not found", id)
                ));
        return videoCardMapping.toDto(videoCard);
    }

    // READ by string id
    public VideoCardDto getVideoCardByStringId(String stringId) {
        Long id = Converter.convertIdStringToLong(stringId);
        return getVideoCardById(id);
    }

    // READ by name
    public VideoCardDto getVideoCardByName(String name) {
        VideoCard videoCard = videoCardRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Video card with name '%s' not found", name)
                ));
        return videoCardMapping.toDto(videoCard);
    }

    // UPDATE
    @Transactional
    public VideoCardDto updateVideoCard(Long id, VideoCardDto videoCardDto) {
        VideoCard existingVideoCard = videoCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Video card with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingVideoCard.getName().equalsIgnoreCase(videoCardDto.getName())) {
            if (videoCardRepository.existsByNameIgnoreCase(videoCardDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Video card '%s' is already taken", videoCardDto.getName())
                );
            }
        }

        // Обновляем все поля
        existingVideoCard.setName(videoCardDto.getName());
        existingVideoCard.setManufacturer(videoCardDto.getManufacturer());
        existingVideoCard.setArchitecture(videoCardDto.getArchitecture());
        existingVideoCard.setClockFrequencyMHz(videoCardDto.getClockFrequencyMHz());
        existingVideoCard.setTurboClockFrequencyMHz(videoCardDto.getTurboClockFrequencyMHz());
        existingVideoCard.setLithographyNm(videoCardDto.getLithographyNm());
        existingVideoCard.setNumberOfAlus(videoCardDto.getNumberOfAlus());
        existingVideoCard.setNumberOfTmus(videoCardDto.getNumberOfTmus());
        existingVideoCard.setNumberOfRops(videoCardDto.getNumberOfRops());
        existingVideoCard.setVramType(videoCardDto.getVramType());
        existingVideoCard.setVramCapacityMb(videoCardDto.getVramCapacityMb());
        existingVideoCard.setVramFrequencyMHz(videoCardDto.getVramFrequencyMHz());
        existingVideoCard.setVramBusBit(videoCardDto.getVramBusBit());
        existingVideoCard.setTdpWatts(videoCardDto.getTdpWatts());
        existingVideoCard.setPcieVersion(videoCardDto.getPcieVersion());

        videoCardRepository.save(existingVideoCard);
        return videoCardMapping.toDto(existingVideoCard);
    }

    // DELETE
    @Transactional
    public void deleteVideoCard(Long id) {
        if (!videoCardRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Video card with id '%d' not found", id)
            );
        }
        videoCardRepository.deleteById(id);
    }

    // Validation
    public void validateAddVideoCard(VideoCardDto videoCardDto) {
        if (videoCardRepository.existsByNameIgnoreCase(videoCardDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Video card '%s' is already taken", videoCardDto.getName())
            );
        }
    }
}