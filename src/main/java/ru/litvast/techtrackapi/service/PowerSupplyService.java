package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.PowerSupplyDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.PowerSupplyMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.PowerSupply;
import ru.litvast.techtrackapi.repository.equipment.computer.PowerSupplyRepository;
import ru.litvast.techtrackapi.util.Converter;

@Service
@RequiredArgsConstructor
public class PowerSupplyService {

    private final PowerSupplyRepository powerSupplyRepository;
    private final PowerSupplyMapping powerSupplyMapping;

    // CREATE
    @Transactional
    public PowerSupplyDto addPowerSupply(PowerSupplyDto powerSupplyDto) {
        if (powerSupplyDto.getId() != null) {
            throw new IllegalArgumentException("To create a power supply, you must specify a name, not an ID");
        }

        validateAddPowerSupply(powerSupplyDto);

        PowerSupply powerSupply = powerSupplyMapping.toEntity(powerSupplyDto);
        powerSupplyRepository.save(powerSupply);
        return powerSupplyMapping.toDto(powerSupply);
    }

    // READ all with pagination
    public Page<PowerSupplyDto> getAllPowerSupplies(Pageable pageable) {
        Page<PowerSupply> powerSupplies = powerSupplyRepository.findAll(pageable);
        if (powerSupplies.isEmpty()) {
            throw new NoEntitiesFoundException("No power supplies found");
        }
        return powerSupplies.map(powerSupplyMapping::toDto);
    }

    // READ by id
    public PowerSupplyDto getPowerSupplyById(Long id) {
        PowerSupply powerSupply = powerSupplyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Power supply with id '%d' not found", id)
                ));
        return powerSupplyMapping.toDto(powerSupply);
    }

    // READ by string id
    public PowerSupplyDto getPowerSupplyByStringId(String stringId) {
        Long id = Converter.convertIdStringToLong(stringId);
        return getPowerSupplyById(id);
    }

    // READ by name
    public PowerSupplyDto getPowerSupplyByName(String name) {
        PowerSupply powerSupply = powerSupplyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Power supply with name '%s' not found", name)
                ));
        return powerSupplyMapping.toDto(powerSupply);
    }

    // UPDATE
    @Transactional
    public PowerSupplyDto updatePowerSupply(Long id, PowerSupplyDto powerSupplyDto) {
        PowerSupply existingPowerSupply = powerSupplyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Power supply with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingPowerSupply.getName().equalsIgnoreCase(powerSupplyDto.getName())) {
            if (powerSupplyRepository.existsByNameIgnoreCase(powerSupplyDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Power supply '%s' is already taken", powerSupplyDto.getName())
                );
            }
        }

        existingPowerSupply.setName(powerSupplyDto.getName());
        existingPowerSupply.setManufacturer(powerSupplyDto.getManufacturer());
        existingPowerSupply.setPowerWatts(powerSupplyDto.getPowerWatts());
        existingPowerSupply.setEfficiency(powerSupplyDto.getEfficiency());
        existingPowerSupply.setFormFactor(powerSupplyDto.getFormFactor());
        existingPowerSupply.setModular(powerSupplyDto.getModular());

        powerSupplyRepository.save(existingPowerSupply);
        return powerSupplyMapping.toDto(existingPowerSupply);
    }

    // DELETE
    @Transactional
    public void deletePowerSupply(Long id) {
        if (!powerSupplyRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Power supply with id '%d' not found", id)
            );
        }
        powerSupplyRepository.deleteById(id);
    }

    // Validation
    public void validateAddPowerSupply(PowerSupplyDto powerSupplyDto) {
        if (powerSupplyRepository.existsByNameIgnoreCase(powerSupplyDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Power supply '%s' is already taken", powerSupplyDto.getName())
            );
        }
    }
}