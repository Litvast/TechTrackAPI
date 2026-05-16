package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoCardService {

    private final VideoCardRepository videoCardRepository;
    private final VideoCardMapping videoCardMapping;

    // CREATE
    @Transactional
    public VideoCardDto addVideoCard(VideoCardDto videoCardDto) {
        log.info("=== НАЧАЛО: Добавление видеокарты ===");
        log.info("Название: {}, TDP: {} Вт", videoCardDto.getName(), videoCardDto.getTdpWatts());

        if (videoCardDto.getId() != null) {
            log.error("Передан ID при создании видеокарты. ID: {}", videoCardDto.getId());
            throw new IllegalArgumentException("To create a video card, you must specify a name, not an ID");
        }

        validateAddVideoCard(videoCardDto);

        VideoCard videoCard = videoCardMapping.toEntity(videoCardDto);
        videoCardRepository.save(videoCard);

        log.info("Видеокарта создана. ID: {}", videoCard.getId());
        log.info("=== УСПЕШНО: Видеокарта добавлена ===");

        return videoCardMapping.toDto(videoCard);
    }

    // READ all with pagination
    public Page<VideoCardDto> getAllVideoCards(Pageable pageable) {
        log.debug("Запрос всех видеокарт с пагинацией");

        Page<VideoCard> videoCards = videoCardRepository.findAll(pageable);
        if (videoCards.isEmpty()) {
            log.warn("Видеокарты не найдены");
            throw new NoEntitiesFoundException("No video cards found");
        }

        log.debug("Найдено {} видеокарт", videoCards.getTotalElements());
        return videoCards.map(videoCardMapping::toDto);
    }

    // READ by id
    public VideoCardDto getVideoCardById(Long id) {
        log.debug("Поиск видеокарты по ID: {}", id);

        VideoCard videoCard = videoCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Видеокарта с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Video card with id '%d' not found", id)
                    );
                });

        return videoCardMapping.toDto(videoCard);
    }

    // READ by string id
    public VideoCardDto getVideoCardByStringId(String stringId) {
        log.debug("Поиск видеокарты по строковому ID: {}", stringId);
        Long id = Converter.convertIdStringToLong(stringId);
        return getVideoCardById(id);
    }

    // READ by name
    public VideoCardDto getVideoCardByName(String name) {
        log.debug("Поиск видеокарты по названию: {}", name);

        VideoCard videoCard = videoCardRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Видеокарта с названием '{}' не найдена", name);
                    return new EntityNotFoundException(
                            String.format("Video card with name '%s' not found", name)
                    );
                });

        return videoCardMapping.toDto(videoCard);
    }

    // UPDATE
    @Transactional
    public VideoCardDto updateVideoCard(Long id, VideoCardDto videoCardDto) {
        log.info("=== НАЧАЛО: Обновление видеокарты ===");
        log.info("ID видеокарты: {}", id);

        VideoCard existingVideoCard = videoCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Видеокарта с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Video card with id '%d' not found", id)
                    );
                });

        if (!existingVideoCard.getName().equalsIgnoreCase(videoCardDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingVideoCard.getName(), videoCardDto.getName());

            if (videoCardRepository.existsByNameIgnoreCase(videoCardDto.getName())) {
                log.warn("Видеокарта с названием '{}' уже существует", videoCardDto.getName());
                throw new IllegalArgumentException(
                        String.format("Video card '%s' is already taken", videoCardDto.getName())
                );
            }
            existingVideoCard.setName(videoCardDto.getName());
        }

        if (videoCardDto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingVideoCard.getManufacturer(), videoCardDto.getManufacturer());
            existingVideoCard.setManufacturer(videoCardDto.getManufacturer());
        }

        if (videoCardDto.getArchitecture() != null) {
            log.info("Изменение архитектуры: {} -> {}", existingVideoCard.getArchitecture(), videoCardDto.getArchitecture());
            existingVideoCard.setArchitecture(videoCardDto.getArchitecture());
        }

        if (videoCardDto.getClockFrequencyMHz() != null) {
            log.info("Изменение тактовой частоты: {} -> {} МГц", existingVideoCard.getClockFrequencyMHz(), videoCardDto.getClockFrequencyMHz());
            existingVideoCard.setClockFrequencyMHz(videoCardDto.getClockFrequencyMHz());
        }

        if (videoCardDto.getTurboClockFrequencyMHz() != null) {
            log.info("Изменение турбо-частоты: {} -> {} МГц", existingVideoCard.getTurboClockFrequencyMHz(), videoCardDto.getTurboClockFrequencyMHz());
            existingVideoCard.setTurboClockFrequencyMHz(videoCardDto.getTurboClockFrequencyMHz());
        }

        if (videoCardDto.getLithographyNm() != null) {
            log.info("Изменение техпроцесса: {} -> {} нм", existingVideoCard.getLithographyNm(), videoCardDto.getLithographyNm());
            existingVideoCard.setLithographyNm(videoCardDto.getLithographyNm());
        }

        if (videoCardDto.getNumberOfAlus() != null) {
            log.info("Изменение количества ALU: {} -> {}", existingVideoCard.getNumberOfAlus(), videoCardDto.getNumberOfAlus());
            existingVideoCard.setNumberOfAlus(videoCardDto.getNumberOfAlus());
        }

        if (videoCardDto.getNumberOfTmus() != null) {
            log.info("Изменение количества TMU: {} -> {}", existingVideoCard.getNumberOfTmus(), videoCardDto.getNumberOfTmus());
            existingVideoCard.setNumberOfTmus(videoCardDto.getNumberOfTmus());
        }

        if (videoCardDto.getNumberOfRops() != null) {
            log.info("Изменение количества ROP: {} -> {}", existingVideoCard.getNumberOfRops(), videoCardDto.getNumberOfRops());
            existingVideoCard.setNumberOfRops(videoCardDto.getNumberOfRops());
        }

        if (videoCardDto.getVramType() != null) {
            log.info("Изменение типа VRAM: {} -> {}", existingVideoCard.getVramType(), videoCardDto.getVramType());
            existingVideoCard.setVramType(videoCardDto.getVramType());
        }

        if (videoCardDto.getVramCapacityMb() != null) {
            log.info("Изменение объёма VRAM: {} -> {} МБ", existingVideoCard.getVramCapacityMb(), videoCardDto.getVramCapacityMb());
            existingVideoCard.setVramCapacityMb(videoCardDto.getVramCapacityMb());
        }

        if (videoCardDto.getVramFrequencyMHz() != null) {
            log.info("Изменение частоты VRAM: {} -> {} МГц", existingVideoCard.getVramFrequencyMHz(), videoCardDto.getVramFrequencyMHz());
            existingVideoCard.setVramFrequencyMHz(videoCardDto.getVramFrequencyMHz());
        }

        if (videoCardDto.getVramBusBit() != null) {
            log.info("Изменение шины VRAM: {} -> {} бит", existingVideoCard.getVramBusBit(), videoCardDto.getVramBusBit());
            existingVideoCard.setVramBusBit(videoCardDto.getVramBusBit());
        }

        if (videoCardDto.getTdpWatts() != null) {
            log.info("Изменение TDP: {} -> {} Вт", existingVideoCard.getTdpWatts(), videoCardDto.getTdpWatts());
            existingVideoCard.setTdpWatts(videoCardDto.getTdpWatts());
        }

        if (videoCardDto.getPcieVersion() != null) {
            log.info("Изменение версии PCIe: {} -> {}", existingVideoCard.getPcieVersion(), videoCardDto.getPcieVersion());
            existingVideoCard.setPcieVersion(videoCardDto.getPcieVersion());
        }

        videoCardRepository.save(existingVideoCard);
        log.info("=== УСПЕШНО: Видеокарта обновлена ===");

        return videoCardMapping.toDto(existingVideoCard);
    }

    // DELETE
    @Transactional
    public void deleteVideoCard(Long id) {
        log.info("=== НАЧАЛО: Удаление видеокарты ===");
        log.info("ID видеокарты: {}", id);

        if (!videoCardRepository.existsById(id)) {
            log.error("Видеокарта с ID {} не найдена", id);
            throw new EntityNotFoundException(
                    String.format("Video card with id '%d' not found", id)
            );
        }

        videoCardRepository.deleteById(id);
        log.info("=== УСПЕШНО: Видеокарта удалена ===");
    }

    // Validation
    public void validateAddVideoCard(VideoCardDto videoCardDto) {
        if (videoCardRepository.existsByNameIgnoreCase(videoCardDto.getName())) {
            log.warn("Видеокарта с названием '{}' уже существует", videoCardDto.getName());
            throw new IllegalArgumentException(
                    String.format("Video card '%s' is already taken", videoCardDto.getName())
            );
        }
    }
}