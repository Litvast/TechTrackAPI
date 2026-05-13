package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
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

    public PowerSupplyDto addPowerSupply(PowerSupplyDto powerSupplyDto) {
        if (powerSupplyDto.getId() != null) {
            throw new IllegalArgumentException("To create a power supply, you must specify a name, not an ID");
        }

        validateAddPowerSupply(powerSupplyDto);

        PowerSupply powerSupply = powerSupplyRepository.save(powerSupplyMapping.toEntity(powerSupplyDto));
        return powerSupplyMapping.toDto(powerSupply);
    }

    public PowerSupplyDto getPowerSupplyById(long id) {
        PowerSupply powerSupply = powerSupplyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Power supply with id '%d' not found", id))
        );

        return powerSupplyMapping.toDto(powerSupply);
    }

    public void validateAddPowerSupply(PowerSupplyDto powerSupplyDto) {
        if (powerSupplyRepository.existsByNameIgnoreCase(powerSupplyDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Processor '%s' is already taken", powerSupplyDto.getName())
            );
        }
    }

    public PowerSupplyDto getPowerSupplyByStringId(String stringId) {
        long id = Converter.convertIdStringToLong(stringId);

        return getPowerSupplyById(id);
    }
}
