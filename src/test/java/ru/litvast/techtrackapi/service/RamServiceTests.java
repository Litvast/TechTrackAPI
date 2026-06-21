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
import ru.litvast.techtrackapi.model.dto.equipment.computer.RamDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.RamMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Ram;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamFormFactor;
import ru.litvast.techtrackapi.model.entity.equipment.computer.RamType;
import ru.litvast.techtrackapi.repository.equipment.computer.RamRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RamServiceTests {

    @Autowired
    private Validator validator;

    @MockitoBean
    private RamRepository ramRepository;

    @Autowired
    private RamService ramService;

    @Autowired
    private RamMapping ramMapping;

    @Test
    void testValidationEmptyRam() {
        // Arrange
        RamDto ramDto = new RamDto();

        // Act
        Set<ConstraintViolation<RamDto>> errors = validator.validate(ramDto);

        // Assert
        List<String> actualMessages = errors.stream().map(ConstraintViolation::getMessage).toList();

        assertThat(actualMessages).contains(
                "Either provide ID (to reference existing) OR name (to create new)"
        );
    }

    @Test
    void testValidationInvalidCapacity() {
        // Arrange
        RamDto ramDto = getRam();
        ramDto.setCapacityMb(-1024);

        // Act
        Set<ConstraintViolation<RamDto>> errors = validator.validate(ramDto);

        // Assert
        assertThat(errors).isNotEmpty();
    }

    @Test
    void testValidationInvalidFrequency() {
        // Arrange
        RamDto ramDto = getRam();
        ramDto.setFrequencyMHz(-6000);

        // Act
        Set<ConstraintViolation<RamDto>> errors = validator.validate(ramDto);

        // Assert
        assertThat(errors).isNotEmpty();
    }

    @Test
    void testAddRamWithIdShouldThrowException() {
        // Arrange
        RamDto ramDto = getRam();
        ramDto.setId(1L);

        // Act & Assert
        assertThatThrownBy(() -> ramService.addRam(ramDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must specify a name, not an ID");

        verify(ramRepository, never()).save(any());
    }

    @Test
    void testAddDuplicateRam() {
        // Arrange
        RamDto ramDto = getRam();

        when(ramRepository.existsByNameIgnoreCase(ramDto.getName()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> ramService.addRam(ramDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(ramRepository, never()).save(any());
    }

    @Test
    void testAddValidRam() {
        // Arrange
        RamDto ramDto = getRam();

        when(ramRepository.existsByNameIgnoreCase(ramDto.getName()))
                .thenReturn(false);
        when(ramRepository.save(any(Ram.class)))
                .thenReturn(ramMapping.toEntity(ramDto));

        // Act
        RamDto actualRam = ramService.addRam(ramDto);

        // Assert
        assertThat(actualRam).isNotNull();
        assertThat(actualRam.getName()).isEqualTo(ramDto.getName());
        assertThat(actualRam.getType()).isEqualTo(ramDto.getType());
        assertThat(actualRam.getCapacityMb()).isEqualTo(ramDto.getCapacityMb());

        verify(ramRepository).existsByNameIgnoreCase(ramDto.getName());
        verify(ramRepository).save(any(Ram.class));
    }

    @Test
    void testAddRamWithNullOptionalFields() {
        // Arrange
        RamDto ramDto = getRam();
        ramDto.setTimings(null);
        ramDto.setVoltage(null);
        ramDto.setXmpSupport(null);
        ramDto.setExpoSupport(null);

        when(ramRepository.existsByNameIgnoreCase(ramDto.getName()))
                .thenReturn(false);
        when(ramRepository.save(any(Ram.class)))
                .thenReturn(ramMapping.toEntity(ramDto));

        // Act
        RamDto actualRam = ramService.addRam(ramDto);

        // Assert
        assertThat(actualRam).isNotNull();
        assertThat(actualRam.getTimings()).isNull();
        assertThat(actualRam.getVoltage()).isNull();

        verify(ramRepository).save(any(Ram.class));
    }

    @Test
    void testAddSomeRamWithEmptyListShouldThrowException() {
        // Arrange
        List<RamDto> ramList = List.of();

        // Act & Assert
        assertThatThrownBy(() -> ramService.addSomeRam(ramList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be empty");

        verify(ramRepository, never()).saveAll(any());
    }

    @Test
    void testAddSomeRamWithIdShouldThrowException() {
        // Arrange
        RamDto ram1 = getRam();
        RamDto ram2 = getRam();
        ram2.setId(1L);

        List<RamDto> ramList = Arrays.asList(ram1, ram2);

        // Act & Assert
        assertThatThrownBy(() -> ramService.addSomeRam(ramList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must specify a name, not an ID");

        verify(ramRepository, never()).saveAll(any());
    }

    @Test
    void testAddSomeValidRams() {
        // Arrange
        RamDto ram1 = getRam();
        ram1.setName("Kingston Fury 32GB DDR5");

        RamDto ram2 = getRam();
        ram2.setName("Corsair Vengeance 16GB DDR5");
        ram2.setCapacityMb(16384);

        List<RamDto> ramList = Arrays.asList(ram1, ram2);
        List<Ram> ramEntities = ramMapping.toEntityList(ramList);

        when(ramRepository.existsByNameIgnoreCase(ram1.getName()))
                .thenReturn(false);
        when(ramRepository.existsByNameIgnoreCase(ram2.getName()))
                .thenReturn(false);
        when(ramRepository.saveAll(anyList()))
                .thenReturn(ramEntities);

        // Act
        List<RamDto> actualRams = ramService.addSomeRam(ramList);

        // Assert
        assertThat(actualRams).isNotNull();
        assertThat(actualRams).hasSize(2);
        assertThat(actualRams.get(0).getName()).isEqualTo(ram1.getName());
        assertThat(actualRams.get(1).getName()).isEqualTo(ram2.getName());

        verify(ramRepository, times(2)).existsByNameIgnoreCase(anyString());
        verify(ramRepository).saveAll(anyList());
    }

    @Test
    void testGetRamByIdSuccess() {
        // Arrange
        Ram ram = ramMapping.toEntity(getRam());
        ram.setId(1L);

        when(ramRepository.findById(1L)).thenReturn(Optional.of(ram));

        // Act
        RamDto actualRam = ramService.getRamById(1L);

        // Assert
        assertThat(actualRam).isNotNull();
        assertThat(actualRam.getId()).isEqualTo(1L);
        assertThat(actualRam.getName()).isEqualTo(ram.getName());

        verify(ramRepository).findById(1L);
    }

    @Test
    void testGetNotExistRamById() {
        // Arrange
        when(ramRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> ramService.getRamById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetRamByNameSuccess() {
        // Arrange
        String name = "Kingston Fury 32GB DDR5";
        Ram ram = ramMapping.toEntity(getRam());
        ram.setName(name);

        when(ramRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(ram));

        // Act
        RamDto actualRam = ramService.getRamByName(name);

        // Assert
        assertThat(actualRam).isNotNull();
        assertThat(actualRam.getName()).isEqualTo(name);
    }

    @Test
    void testGetNotExistRamByName() {
        // Arrange
        String name = "NonExistentRAM";
        when(ramRepository.findByNameIgnoreCase(name)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> ramService.getRamByName(name))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testGetAllRamsSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Ram ram1 = ramMapping.toEntity(getRam());
        Ram ram2 = ramMapping.toEntity(getRam());
        ram2.setName("Corsair Vengeance 16GB DDR5");
        ram2.setCapacityMb(16384);

        Page<Ram> ramPage = new PageImpl<>(List.of(ram1, ram2), pageable, 2);

        when(ramRepository.findAll(pageable)).thenReturn(ramPage);

        // Act
        Page<RamDto> result = ramService.getAllRams(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);

        verify(ramRepository).findAll(pageable);
    }

    @Test
    void testGetAllRamsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ram> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(ramRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act & Assert
        assertThatThrownBy(() -> ramService.getAllRams(pageable))
                .isInstanceOf(NoEntitiesFoundException.class)
                .hasMessageContaining("No RAMs found");
    }

    @Test
    void testUpdateRamSuccess() {
        // Arrange
        Ram existingRam = ramMapping.toEntity(getRam());
        existingRam.setId(1L);

        RamDto updatedRam = getRam();
        updatedRam.setName("G.Skill Trident Z5 RGB 32GB DDR5");
        updatedRam.setFrequencyMHz(6400);
        updatedRam.setTimings("32-39-39-102");
        updatedRam.setVoltage(1.4);

        when(ramRepository.findById(1L)).thenReturn(Optional.of(existingRam));
        when(ramRepository.existsByNameIgnoreCase(updatedRam.getName()))
                .thenReturn(false);
        when(ramRepository.save(any(Ram.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RamDto actualRam = ramService.updateRam(1L, updatedRam);

        // Assert
        assertThat(actualRam).isNotNull();
        assertThat(actualRam.getName()).isEqualTo("G.Skill Trident Z5 RGB 32GB DDR5");
        assertThat(actualRam.getFrequencyMHz()).isEqualTo(6400);
        assertThat(actualRam.getTimings()).isEqualTo("32-39-39-102");
        assertThat(actualRam.getVoltage()).isEqualTo(1.4);
        assertThat(actualRam.getCapacityMb()).isEqualTo(32768);

        verify(ramRepository).findById(1L);
        verify(ramRepository).save(any(Ram.class));
    }

    @Test
    void testUpdateRamNotFound() {
        // Arrange
        RamDto updatedRam = getRam();

        when(ramRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> ramService.updateRam(999L, updatedRam))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testUpdateRamWithDuplicateName() {
        // Arrange
        Ram existingRam = ramMapping.toEntity(getRam());
        existingRam.setId(1L);

        RamDto updatedRam = getRam();
        updatedRam.setName("Corsair Vengeance 16GB DDR5");

        when(ramRepository.findById(1L)).thenReturn(Optional.of(existingRam));
        when(ramRepository.existsByNameIgnoreCase("Corsair Vengeance 16GB DDR5"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> ramService.updateRam(1L, updatedRam))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already taken");

        verify(ramRepository, never()).save(any());
    }

    @Test
    void testUpdateRamPartialUpdate() {
        // Arrange
        Ram existingRam = ramMapping.toEntity(getRam());
        existingRam.setId(1L);

        RamDto updatedRam = new RamDto();
        updatedRam.setFrequencyMHz(7000);
        updatedRam.setVoltage(1.45);

        when(ramRepository.findById(1L)).thenReturn(Optional.of(existingRam));
        when(ramRepository.save(any(Ram.class)))
                .thenAnswer(invocation -> {
                    Ram savedRam = invocation.getArgument(0);
                    return savedRam;
                });

        // Act
        RamDto actualRam = ramService.updateRam(1L, updatedRam);

        // Assert
        assertThat(actualRam).isNotNull();
        assertThat(actualRam.getFrequencyMHz()).isEqualTo(7000);
        assertThat(actualRam.getVoltage()).isEqualTo(1.45);
        assertThat(actualRam.getName()).isEqualTo("Kingston Fury 32GB DDR5");
        assertThat(actualRam.getCapacityMb()).isEqualTo(32768);

        verify(ramRepository).save(any(Ram.class));
    }

    @Test
    void testDeleteRamSuccess() {
        // Arrange
        when(ramRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ramRepository).deleteById(1L);

        // Act
        ramService.deleteRam(1L);

        // Assert
        verify(ramRepository).existsById(1L);
        verify(ramRepository).deleteById(1L);
    }

    @Test
    void testDeleteRamNotFound() {
        // Arrange
        when(ramRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> ramService.deleteRam(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");

        verify(ramRepository, never()).deleteById(any());
    }

    private RamDto getRam() {
        RamDto ram = new RamDto();
        ram.setName("Kingston Fury 32GB DDR5");
        ram.setManufacturer("Kingston");
        ram.setType(RamType.DDR5);
        ram.setFormFactor(RamFormFactor.DIMM);
        ram.setCapacityMb(32768);
        ram.setFrequencyMHz(6000);
        ram.setEcc(false);
        ram.setRegistered(false);
        ram.setTimings("36-38-38-80");
        ram.setVoltage(1.35);
        ram.setXmpSupport(true);
        ram.setExpoSupport(false);
        ram.setDualRank(false);
        ram.setOnDieEcc(false);
        return ram;
    }
}