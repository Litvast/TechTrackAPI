package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.RamDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.RamMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;
import ru.litvast.techtrackapi.repository.equipment.computer.RamRepository;
import ru.litvast.techtrackapi.util.Converter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RamService {

    private final RamRepository ramRepository;
    private final RamMapping ramMapping;

    // CREATE single
    @Transactional
    public RamDto addRam(RamDto ramDto) {
        if (ramDto == null) {
            throw new IllegalArgumentException("RAM cannot be null");
        }

        if (ramDto.getId() != null) {
            throw new IllegalArgumentException("To create a RAM, you must specify a name, not an ID");
        }

        validateAddRam(ramDto);

        Ram ram = ramMapping.toEntity(ramDto);
        ramRepository.save(ram);
        return ramMapping.toDto(ram);
    }

    // CREATE multiple
    @Transactional
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

    // READ all with pagination
    public Page<RamDto> getAllRams(Pageable pageable) {
        Page<Ram> rams = ramRepository.findAll(pageable);
        if (rams.isEmpty()) {
            throw new NoEntitiesFoundException("No RAMs found");
        }
        return rams.map(ramMapping::toDto);
    }

    // READ by id
    public RamDto getRamById(Long id) {
        Ram ram = ramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("RAM with id '%d' not found", id)
                ));
        return ramMapping.toDto(ram);
    }

    // READ by string id
    public RamDto getRamByStringId(String stringId) {
        Long id = Converter.convertIdStringToLong(stringId);
        return getRamById(id);
    }

    // READ by name
    public RamDto getRamByName(String name) {
        Ram ram = ramRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("RAM with name '%s' not found", name)
                ));
        return ramMapping.toDto(ram);
    }

    // UPDATE
    @Transactional
    public RamDto updateRam(Long id, RamDto ramDto) {
        Ram existingRam = ramRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("RAM with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingRam.getName().equalsIgnoreCase(ramDto.getName())) {
            if (ramRepository.existsByNameIgnoreCase(ramDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("RAM '%s' is already taken", ramDto.getName())
                );
            }
        }

        // Обновляем все поля
        existingRam.setName(ramDto.getName());
        existingRam.setManufacturer(ramDto.getManufacturer());
        existingRam.setType(ramDto.getType());
        existingRam.setFormFactor(ramDto.getFormFactor());
        existingRam.setCapacityMb(ramDto.getCapacityMb());
        existingRam.setFrequencyMHz(ramDto.getFrequencyMHz());
        existingRam.setTimings(ramDto.getTimings());
        existingRam.setVoltage(ramDto.getVoltage());
        existingRam.setEcc(ramDto.getEcc());
        existingRam.setRegistered(ramDto.getRegistered());
        existingRam.setXmpSupport(ramDto.getXmpSupport());
        existingRam.setExpoSupport(ramDto.getExpoSupport());
        existingRam.setDualRank(ramDto.getDualRank());
        existingRam.setOnDieEcc(ramDto.getOnDieEcc());

        ramRepository.save(existingRam);
        return ramMapping.toDto(existingRam);
    }

    // DELETE
    @Transactional
    public void deleteRam(Long id) {
        if (!ramRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("RAM with id '%d' not found", id)
            );
        }
        ramRepository.deleteById(id);
    }

    // Validation
    public void validateAddRam(RamDto ramDto) {
        if (ramRepository.existsByNameIgnoreCase(ramDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("RAM '%s' is already taken", ramDto.getName())
            );
        }
    }
}