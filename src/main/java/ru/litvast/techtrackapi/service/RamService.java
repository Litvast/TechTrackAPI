package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.RamDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.RamMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;
import ru.litvast.techtrackapi.repository.equipment.computer.RamRepository;
import ru.litvast.techtrackapi.util.Converter;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RamService {

    private final RamRepository ramRepository;
    private final RamMapping ramMapping;

    public RamDto addRam(RamDto ramDto) {
        if (ramDto == null) {
            throw new IllegalArgumentException("RAM cannot be null");
        }

        if (ramDto.getId() != null) {
            throw new IllegalArgumentException("To create a RAM, you must specify a name, not an ID");
        }

        validateAddRam(ramDto);

        Ram ram = ramRepository.save(ramMapping.toEntity(ramDto));
        return ramMapping.toDto(ram);
    }

    public List<RamDto> addSomeRam(List<RamDto> ramDtoList) {
        if (ramDtoList == null || ramDtoList.isEmpty()) {
            throw new IllegalArgumentException("RAM list cannot be empty");
        }

        ramDtoList.forEach(ramDto -> {
            if (ramDto.getId() != null) {
                throw new IllegalArgumentException("To create a RAM, you must specify a name, not an ID");
            }

            validateAddRam(ramDto);
        });

        List<Ram> ramList = ramMapping.toEntityList(ramDtoList);

        List<Ram> rams = ramRepository.saveAll(ramList);

        return ramMapping.toDtoList(rams);
    }

    public RamDto getRamById(long id) {
        Ram ram = ramRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Ram with id '%d' not found", id))
        );

        return ramMapping.toDto(ram);

    }

    public void validateAddRam(RamDto ramDto) {
        if (ramRepository.existsByNameIgnoreCase(ramDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("RAM '%s' is already taken", ramDto.getName())
            );
        }
    }

    public RamDto getRamByStringId(String stringId) {
        long id = Converter.convertIdStringToLong(stringId);

        return getRamById(id);
    }
}
