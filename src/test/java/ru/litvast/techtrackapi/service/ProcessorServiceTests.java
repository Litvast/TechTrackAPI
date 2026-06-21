package ru.litvast.techtrackapi.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.*;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.ProcessorMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Processor;
import ru.litvast.techtrackapi.repository.equipment.computer.ProcessorRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProcessorServiceTests {

    @Autowired
    private Validator validator;

    @MockitoBean
    private ProcessorRepository processorRepository;

    @MockitoBean
    private CpuSocketService cpuSocketService;

    @MockitoBean
    private CpuArchitectureService cpuArchitectureService;

    @Autowired
    private ProcessorService processorService;

    @Autowired
    private ProcessorMapping processorMapping;

    @Test
    void testValidationEmptyProcessor() {
        // Arrange
        ProcessorDto processorDto = new ProcessorDto();

        // Act
        Set<ConstraintViolation<ProcessorDto>> errors = validator.validate(processorDto);

        // Assert
        List<String> actualMessages = errors.stream().map(ConstraintViolation::getMessage).toList();

        assertThat(actualMessages).contains(
                "Name is required",
                "Manufacturer is required"
        );
    }

    @Test
    void testValidationInvalidClockFrequency() {
        // Arrange
        ProcessorDto processorDto = getProcessor();
        processorDto.setClockFrequencyGHz(-1.0);

        // Act
        Set<ConstraintViolation<ProcessorDto>> errors = validator.validate(processorDto);

        // Assert
        assertThat(errors).isNotEmpty();
    }

    @Test
    void testAddProcessorWithIdShouldThrowException() {
        // Arrange
        ProcessorDto processorDto = getProcessor();
        processorDto.setId(1L);

        // Act && Assert
        assertThatThrownBy(() -> processorService.addProcessor(processorDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must specify a name, not an ID");

        verify(processorRepository, never()).save(any());
    }

    @Test
    void testAddDuplicateProcessor() {
        // Arrange
        String processorName = "Intel Core i7-13700K";
        ProcessorDto processorDto = getProcessor();
        processorDto.setName(processorName);

        when(processorRepository.existsByNameIgnoreCase(processorName))
                .thenReturn(true);

        // Act && Assert
        assertThatThrownBy(() -> processorService.addProcessor(processorDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(processorRepository, never()).save(any());
    }

    @Test
    void testAddValidProcessor() {
        // Arrange
        ProcessorDto processorDto = getProcessor();

        when(processorRepository.existsByNameIgnoreCase(processorDto.getName()))
                .thenReturn(false);
        when(cpuSocketService.addCpuSocket(processorDto.getSocket()))
                .thenReturn(processorDto.getSocket());
        when(cpuArchitectureService.addCpuArchitecture(processorDto.getArchitecture()))
                .thenReturn(processorDto.getArchitecture());
        when(processorRepository.save(any(Processor.class)))
                .thenReturn(processorMapping.toEntity(processorDto));

        // Act
        ProcessorDto actualProcessor = processorService.addProcessor(processorDto);

        // Assert
        assertThat(actualProcessor).isNotNull();
        assertThat(actualProcessor.getName()).isEqualTo(processorDto.getName());
        assertThat(actualProcessor.getSocket().getName()).isEqualTo(processorDto.getSocket().getName());
        assertThat(actualProcessor.getNumberOfCores()).isEqualTo(processorDto.getNumberOfCores());

        verify(processorRepository).existsByNameIgnoreCase(processorDto.getName());
        verify(cpuSocketService).addCpuSocket(any(CpuSocketDto.class));
        verify(cpuArchitectureService).addCpuArchitecture(any(CpuArchitectureDto.class));
        verify(processorRepository).save(any(Processor.class));
    }

    @Test
    void testAddProcessorWithExistingSocketAndArchitecture() {
        // Arrange
        ProcessorDto processorDto = getProcessor();
        processorDto.getSocket().setId(1L);
        processorDto.getArchitecture().setId(1L);

        when(processorRepository.existsByNameIgnoreCase(processorDto.getName()))
                .thenReturn(false);
        when(cpuSocketService.getCpuSocketById(1L))
                .thenReturn(processorDto.getSocket());
        when(cpuArchitectureService.getCpuArchitectureById(1L))
                .thenReturn(processorDto.getArchitecture());
        when(processorRepository.save(any(Processor.class)))
                .thenReturn(processorMapping.toEntity(processorDto));

        // Act
        ProcessorDto actualProcessor = processorService.addProcessor(processorDto);

        // Assert
        assertThat(actualProcessor).isNotNull();

        verify(cpuSocketService, never()).addCpuSocket(any());
        verify(cpuArchitectureService, never()).addCpuArchitecture(any());
        verify(cpuSocketService).getCpuSocketById(1L);
        verify(cpuArchitectureService).getCpuArchitectureById(1L);
    }

    @Test
    void testGetProcessorByIdSuccess() {
        // Arrange
        Processor processor = processorMapping.toEntity(getProcessor());
        processor.setId(1L);

        when(processorRepository.findById(1L)).thenReturn(Optional.of(processor));

        // Act
        ProcessorDto actualProcessor = processorService.getProcessorById(1L);

        // Assert
        assertThat(actualProcessor).isNotNull();
        assertThat(actualProcessor.getName()).isEqualTo(processor.getName());

        verify(processorRepository).findById(1L);
    }

    @Test
    void testGetNotExistProcessorById() {
        // Arrange
        when(processorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act && Assert
        assertThatThrownBy(() -> processorService.getProcessorById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetProcessorByStringIdSuccess() {
        // Arrange
        Processor processor = processorMapping.toEntity(getProcessor());
        processor.setId(1L);

        when(processorRepository.findById(1L)).thenReturn(Optional.of(processor));

        // Act
        ProcessorDto actualProcessor = processorService.getProcessorByStringId("1");

        // Assert
        assertThat(actualProcessor).isNotNull();
        assertThat(actualProcessor.getName()).isEqualTo(processor.getName());
    }

    @Test
    void testGetProcessorByNameSuccess() {
        // Arrange
        String name = "Intel Core i7-13700K";
        Processor processor = processorMapping.toEntity(getProcessor());
        processor.setName(name);

        when(processorRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(processor));

        // Act
        ProcessorDto actualProcessor = processorService.getProcessorByName(name);

        // Assert
        assertThat(actualProcessor).isNotNull();
        assertThat(actualProcessor.getName()).isEqualTo(name);
    }

    @Test
    void testGetNotExistProcessorByName() {
        // Arrange
        String name = "NonExistentCPU";
        when(processorRepository.findByNameIgnoreCase(name)).thenReturn(Optional.empty());

        // Act && Assert
        assertThatThrownBy(() -> processorService.getProcessorByName(name))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetAllProcessorsSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Processor processor1 = processorMapping.toEntity(getProcessor());
        Processor processor2 = processorMapping.toEntity(getProcessor());
        processor2.setName("AMD Ryzen 9 7950X");

        Page<Processor> processorPage = new PageImpl<>(List.of(processor1, processor2), pageable, 2);

        when(processorRepository.findAll(pageable)).thenReturn(processorPage);

        // Act
        Page<ProcessorDto> result = processorService.getAllProcessors(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        verify(processorRepository).findAll(pageable);
    }

    @Test
    void testGetAllProcessorsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Processor> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(processorRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act && Assert
        assertThatThrownBy(() -> processorService.getAllProcessors(pageable))
                .isInstanceOf(NoEntitiesFoundException.class)
                .hasMessageContaining("No processors found");
    }

    @Test
    void testUpdateProcessorSuccess() {
        // Arrange
        Processor existingProcessor = processorMapping.toEntity(getProcessor());
        existingProcessor.setId(1L);

        ProcessorDto updatedProcessor = getProcessor();
        updatedProcessor.setId(1L);
        updatedProcessor.setName("Intel Core i9-14900K");
        updatedProcessor.setNumberOfCores(24);
        updatedProcessor.setTurboClockFrequencyGHz(6.0);
        updatedProcessor.setTdpWatts(125);

        when(processorRepository.findById(1L)).thenReturn(Optional.of(existingProcessor));
        when(processorRepository.existsByNameIgnoreCase(updatedProcessor.getName()))
                .thenReturn(false);
        when(cpuSocketService.addCpuSocket(updatedProcessor.getSocket()))
                .thenReturn(updatedProcessor.getSocket());
        when(cpuArchitectureService.addCpuArchitecture(updatedProcessor.getArchitecture()))
                .thenReturn(updatedProcessor.getArchitecture());
        when(processorRepository.save(any(Processor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProcessorDto actualProcessor = processorService.updateProcessor(1L, updatedProcessor);

        // Assert
        assertThat(actualProcessor).isNotNull();
        assertThat(actualProcessor.getName()).isEqualTo("Intel Core i9-14900K");
        assertThat(actualProcessor.getNumberOfCores()).isEqualTo(24);
        assertThat(actualProcessor.getTurboClockFrequencyGHz()).isEqualTo(6.0);
        assertThat(actualProcessor.getClockFrequencyGHz()).isEqualTo(3.4); // unchanged

        verify(processorRepository).findById(1L);
        verify(processorRepository).save(any(Processor.class));
    }

    @Test
    void testUpdateProcessorNotFound() {
        // Arrange
        ProcessorDto updatedProcessor = getProcessor();
        updatedProcessor.setId(999L);

        when(processorRepository.findById(999L)).thenReturn(Optional.empty());

        // Act && Assert
        assertThatThrownBy(() -> processorService.updateProcessor(999L, updatedProcessor))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testUpdateProcessorWithDuplicateName() {
        // Arrange
        Processor existingProcessor = processorMapping.toEntity(getProcessor());
        existingProcessor.setId(1L);

        ProcessorDto updatedProcessor = getProcessor();
        updatedProcessor.setId(1L);
        updatedProcessor.setName("AMD Ryzen 9 7950X"); // имя, которое уже существует

        when(processorRepository.findById(1L)).thenReturn(Optional.of(existingProcessor));
        when(processorRepository.existsByNameIgnoreCase("AMD Ryzen 9 7950X"))
                .thenReturn(true);

        // Act && Assert
        assertThatThrownBy(() -> processorService.updateProcessor(1L, updatedProcessor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(processorRepository, never()).save(any());
    }

    @Test
    void testUpdateProcessorPartialUpdate() {
        // Arrange
        Processor existingProcessor = processorMapping.toEntity(getProcessor());
        existingProcessor.setId(1L);

        ProcessorDto updatedProcessor = new ProcessorDto();
        updatedProcessor.setId(1L);
        updatedProcessor.setTdpWatts(150); // обновляем только TDP
        updatedProcessor.setLithographyNm(7); // обновляем только техпроцесс

        when(processorRepository.findById(1L)).thenReturn(Optional.of(existingProcessor));
        when(processorRepository.save(any(Processor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProcessorDto actualProcessor = processorService.updateProcessor(1L, updatedProcessor);

        // Assert
        assertThat(actualProcessor).isNotNull();
        assertThat(actualProcessor.getTdpWatts()).isEqualTo(150);
        assertThat(actualProcessor.getLithographyNm()).isEqualTo(7);
        assertThat(actualProcessor.getName()).isEqualTo("Intel Core i7-13700K"); // unchanged
        assertThat(actualProcessor.getNumberOfCores()).isEqualTo(16); // unchanged

        verify(processorRepository).save(any(Processor.class));
    }

    @Test
    void testDeleteProcessorSuccess() {
        // Arrange
        when(processorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(processorRepository).deleteById(1L);

        // Act
        processorService.deleteProcessor(1L);

        // Assert
        verify(processorRepository).existsById(1L);
        verify(processorRepository).deleteById(1L);
    }

    @Test
    void testDeleteProcessorNotFound() {
        // Arrange
        when(processorRepository.existsById(999L)).thenReturn(false);

        // Act && Assert
        assertThatThrownBy(() -> processorService.deleteProcessor(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");

        verify(processorRepository, never()).deleteById(any());
    }

    private ProcessorDto getProcessor() {
        CpuSocketDto socket = new CpuSocketDto();
        socket.setName("LGA1700");
        socket.setManufacturer("Intel");

        CpuArchitectureDto architecture = new CpuArchitectureDto();
        architecture.setName("Raptor Lake");
        architecture.setBitWidth(64);

        ProcessorDto processor = new ProcessorDto();
        processor.setName("Intel Core i7-13700K");
        processor.setManufacturer("Intel");
        processor.setSocket(socket);
        processor.setArchitecture(architecture);
        processor.setClockFrequencyGHz(3.4);
        processor.setTurboClockFrequencyGHz(5.4);
        processor.setNumberOfCores(16);
        processor.setNumberOfThreads(24);
        processor.setL1CacheKB(1024);
        processor.setL2CacheKB(2048);
        processor.setL3CacheMB(30);
        processor.setTdpWatts(125);
        processor.setLithographyNm(10);

        return processor;
    }

    private Processor getProcessorEntity() {
        return processorMapping.toEntity(getProcessor());
    }
}