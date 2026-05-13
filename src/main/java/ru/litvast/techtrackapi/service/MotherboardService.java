package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.MotherboardDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.MotherboardMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Motherboard;
import ru.litvast.techtrackapi.repository.equipment.computer.MotherboardRepository;
import ru.litvast.techtrackapi.util.Converter;

@Service
@RequiredArgsConstructor
public class MotherboardService {

    private final MotherboardRepository motherboardRepository;
    private final MotherboardMapping motherboardMapping;

    public MotherboardDto addMotherboard(MotherboardDto motherboardDto) {
        if (motherboardDto.getId() != null) {
            throw new IllegalArgumentException("To create a motherboard, you must specify a name, not an ID");
        }

        validateAddMotherboard(motherboardDto);

        Motherboard motherboard = motherboardRepository.save(motherboardMapping.toEntity(motherboardDto));
        return motherboardMapping.toDto(motherboard);
    }

    public MotherboardDto getMotherboardById(long id) {
        Motherboard motherboard = motherboardRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Motherboard with id '%d' not found", id))
        );

        return motherboardMapping.toDto(motherboard);
    }

    public void validateAddMotherboard(MotherboardDto motherboardDto) {
        if (motherboardRepository.existsByNameIgnoreCase(motherboardDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Motherboard '%s' is already taken", motherboardDto.getName())
            );
        }
    }

    public MotherboardDto getProcessorByStringId(String stringId) {
        long id = Converter.convertIdStringToLong(stringId);

        return getMotherboardById(id);
    }
}
