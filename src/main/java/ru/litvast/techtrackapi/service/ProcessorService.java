package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.ProcessorDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.ProcessorMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Processor;
import ru.litvast.techtrackapi.repository.equipment.computer.ProcessorRepository;
import ru.litvast.techtrackapi.util.Converter;

@Service
@RequiredArgsConstructor
public class ProcessorService {

    private final ProcessorRepository processorRepository;
    private final ProcessorMapping processorMapping;

    public ProcessorDto addProcessor(ProcessorDto processorDto) {
        if (processorDto.getId() != null) {
            throw new IllegalArgumentException("To create a processor, you must specify a name, not an ID");
        }

        validateAddProcessor(processorDto);

        Processor processor = processorRepository.save(processorMapping.toEntity(processorDto));
        return processorMapping.toDto(processor);
    }

    public ProcessorDto getProcessorById(long id) {
        Processor processor = processorRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Processor with id '%d' not found", id))
        );

        return processorMapping.toDto(processor);
    }

    public void validateAddProcessor(ProcessorDto processorDto) {
        if (processorRepository.existsByNameIgnoreCase(processorDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Processor '%s' is already taken", processorDto.getName())
            );
        }
    }

    public ProcessorDto getProcessorByStringId(String stringId) {
        long id = Converter.convertIdStringToLong(stringId);

        return getProcessorById(id);
    }
}
