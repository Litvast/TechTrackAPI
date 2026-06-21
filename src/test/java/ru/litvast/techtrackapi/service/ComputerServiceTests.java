package ru.litvast.techtrackapi.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.*;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.ComputerMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.*;
import ru.litvast.techtrackapi.repository.equipment.computer.ComputerRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ComputerServiceTests {

    @Autowired
    private Validator validator;

    @MockitoBean
    private ComputerRepository computerRepository;

    @MockitoBean
    private CpuSocketService cpuSocketService;

    @Autowired
    private ComputerService computerService;

    @MockitoBean
    private ProcessorService processorService;

    @MockitoBean
    private MotherboardService motherboardService;

    @MockitoBean
    private VideoCardService videoCardService;

    @MockitoBean
    private PowerSupplyService powerSupplyService;

    @MockitoBean
    private RamService ramService;

    @MockitoBean
    private StorageDeviceService storageDeviceService;

    @Autowired
    private ComputerMapping computerMapping;

    @Test
    void testValidationEmptyComputer() {
        // Arrange
        ComputerDto computerDto = new ComputerDto();

        // Act
        Set<ConstraintViolation<ComputerDto>> errors = validator.validate(computerDto);

        // Assert
        List<String> actualMessages = errors.stream().map(ConstraintViolation::getMessage).toList();
        List<String> expectedMessages = List.of(
                "Processor is required",
                "Motherboard is required",
                "At least one RAM is required",
                "At least one storage device is required",
                "Power supply is required",
                "Either provide ID (to reference existing) OR name (to create new)");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    @Test
    void testValidationComputerWithIDandName() {
        // Arrange
        ComputerDto computerDto = getComputer();
        computerDto.setId(1L);

        // Act
        Set<ConstraintViolation<ComputerDto>> errors = validator.validate(computerDto);

        // Assert
        List<String> actualMessages = errors.stream().map(ConstraintViolation::getMessage).toList();
        List<String> expectedMessages = List.of(
                "Either provide ID (to reference existing) OR name (to create new)");

        assertThat(actualMessages).containsExactlyInAnyOrderElementsOf(expectedMessages);
    }

    @Test
    void testAddDuplicateComputer() {
        // Arrange
        String computerName = "Офисный компьютер HP";

        ComputerDto computerDto = new ComputerDto();
        computerDto.setName(computerName);

        when(computerRepository.existsByNameIgnoreCase(computerName))
                .thenReturn(true);

        // Act && Assert
        assertThrows(IllegalArgumentException.class, () ->
                computerService.addComputer(computerDto));
    }

    @Test
    void testAddValidComputer() {
        // Arrange
        ComputerDto computerDto = getComputer();

        when(computerRepository.existsByNameIgnoreCase(computerDto.getName()))
                .thenReturn(false);

        when(cpuSocketService.addCpuSocket(computerDto.getProcessor().getSocket()))
                .thenReturn(computerDto.getProcessor().getSocket());

        when(processorService.addProcessor(computerDto.getProcessor()))
                .thenReturn(computerDto.getProcessor());

        when(motherboardService.addMotherboard(computerDto.getMotherboard()))
                .thenReturn(computerDto.getMotherboard());

        when(videoCardService.addVideoCard(computerDto.getVideoCard()))
                .thenReturn(computerDto.getVideoCard());

        when(powerSupplyService.addPowerSupply(computerDto.getPowerSupply()))
                .thenReturn(computerDto.getPowerSupply());

        when(ramService.getRamById(computerDto.getRams().get(1).getId()))
                .thenReturn(computerDto.getRams().get(1));
        when(ramService.addRam(computerDto.getRams().getFirst()))
                .thenReturn(computerDto.getRams().getFirst());

        when(storageDeviceService.addStorageDevice(computerDto.getStorageDevices().getFirst()))
                .thenReturn(computerDto.getStorageDevices().getFirst());

        when(computerRepository.save(computerMapping.toEntity(computerDto)))
                .thenReturn(computerMapping.toEntity(computerDto));

        // Act
        ComputerDto actualComputer = computerService.addComputer(computerDto);

        // Assert
        assertThat(actualComputer).isNotNull();
        assertThat(actualComputer.getName()).isEqualTo(computerDto.getName());
        assertThat(actualComputer.getRams().size()).isEqualTo(computerDto.getRams().size());

        // Проверяем, что все нужные методы были вызваны
        verify(computerRepository).existsByNameIgnoreCase(computerDto.getName());
        verify(processorService).addProcessor(any(ProcessorDto.class));
        verify(motherboardService).addMotherboard(any(MotherboardDto.class));
        verify(videoCardService).addVideoCard(any(VideoCardDto.class));
        verify(powerSupplyService).addPowerSupply(any(PowerSupplyDto.class));
        verify(ramService, times(1)).addRam(any(RamDto.class));
        verify(storageDeviceService).addStorageDevice(any(StorageDeviceDto.class));
        verify(computerRepository).save(any(Computer.class));
    }

    @Test
    void testGetComputerById() {
        // Arrange
        Computer computer = computerMapping.toEntity(getComputer());

        when(computerRepository.findById(1L))
                .thenReturn(Optional.of(computer));

        // Act
        ComputerDto actualComputer = computerService.getComputerById(1L);

        // Assert
        assertThat(actualComputer)
                .isNotNull();
        assertThat(actualComputer.getName())
                .isEqualTo(computer.getName());

        verify(computerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetNotExistComputerById() {
        // Arrange
        when(computerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(EntityNotFoundException.class,() ->
                computerService.getComputerById(1L));

    }

    @Test
    void testUpdateComputer() {
        // Arrange
        Computer existingComputer = computerMapping.toEntity(getComputer());
        ComputerUpdateDto updatedComputer = computerMapping.toUpdateDto(getComputer());

        existingComputer.setId(1L);

        // Добавление изменений в обновлённый объект компьютера
        updatedComputer.getProcessor().setName("i7-13850HX");
        updatedComputer.getProcessor().setTurboClockFrequencyGHz(5.8);
        updatedComputer.getProcessor().setNumberOfCores(20);
        updatedComputer.getProcessor().setNumberOfThreads(28);

        updatedComputer.getMotherboard().setName("MSI MEG Z790 ACE");
        updatedComputer.getMotherboard().setManufacturer("MSI");
        updatedComputer.getMotherboard().setChipset("Z790");

        RamDto ram = new RamDto();
        ram.setName("G.Skill Trident Z5 RGB 32GB DDR5");
        ram.setType(RamType.DDR5);
        ram.setFormFactor(RamFormFactor.DIMM);
        ram.setCapacityMb(32768);
        ram.setFrequencyMHz(6400);
        ram.setEcc(false);
        ram.setTimings("32-39-39-102");
        ram.setVoltage(1.4);
        updatedComputer.getRams().add(ram);

        updatedComputer.getVideoCard().setName("NVIDIA RTX 4080 Super");
        updatedComputer.getVideoCard().setManufacturer("NVIDIA");
        updatedComputer.getVideoCard().setTdpWatts(320);

        updatedComputer.setPowerSupply(null);

        updatedComputer.setName("Игровой Asus");
        updatedComputer.setId(1L);

        when(computerRepository.findById(updatedComputer.getId()))
                .thenReturn(Optional.of(existingComputer));
        when(computerRepository.existsByNameIgnoreCase(updatedComputer.getName()))
                .thenReturn(false);

        when(processorService.addProcessor(updatedComputer.getProcessor()))
                .thenReturn(updatedComputer.getProcessor());

        when(motherboardService.addMotherboard(updatedComputer.getMotherboard()))
                .thenReturn(updatedComputer.getMotherboard());

        when(videoCardService.addVideoCard(updatedComputer.getVideoCard()))
                .thenReturn(updatedComputer.getVideoCard());

        when(ramService.addRam(updatedComputer.getRams().getFirst()))
                .thenReturn(updatedComputer.getRams().getFirst());
        when(ramService.getRamById(updatedComputer.getRams().get(1).getId()))
                .thenReturn(updatedComputer.getRams().get(1));
        when(ramService.addRam(updatedComputer.getRams().get(2)))
                .thenReturn(updatedComputer.getRams().get(2));

        when(storageDeviceService.addStorageDevice(updatedComputer.getStorageDevices().getFirst()))
                .thenReturn(updatedComputer.getStorageDevices().getFirst());

        when(computerRepository.save(computerMapping.toEntity(any(ComputerDto.class))))
                .thenAnswer(invocation -> {
                    return invocation.getArgument(0);
                });
        // Act
        ComputerDto actualComputer = computerService.updateComputer(updatedComputer);

        // Assert
        assertThat(actualComputer).isNotNull();
        assertThat(actualComputer.getName()).isEqualTo(updatedComputer.getName());

        assertThat(actualComputer.getProcessor().getName())
                .isEqualTo(updatedComputer.getProcessor().getName());
        assertThat(actualComputer.getProcessor().getNumberOfCores())
                .isEqualTo(updatedComputer.getProcessor().getNumberOfCores());
        assertThat(actualComputer.getProcessor().getClockFrequencyGHz())
                .isEqualTo(existingComputer.getProcessor().getClockFrequencyGHz());

        assertThat(actualComputer.getMotherboard().getName())
                .isEqualTo(updatedComputer.getMotherboard().getName());
        assertThat(actualComputer.getMotherboard().getManufacturer())
                .isEqualTo(updatedComputer.getMotherboard().getManufacturer());
        assertThat(actualComputer.getMotherboard().getSocket().getName())
                .isEqualTo(updatedComputer.getMotherboard().getSocket().getName());

        assertThat(actualComputer.getVideoCard().getName())
                .isEqualTo(updatedComputer.getVideoCard().getName());
        assertThat(actualComputer.getVideoCard().getTdpWatts())
                .isEqualTo(updatedComputer.getVideoCard().getTdpWatts());

        assertThat(actualComputer.getPowerSupply().getName())
                .isEqualTo(existingComputer.getPowerSupply().getName());
        assertThat(actualComputer.getPowerSupply().getEfficiency())
                .isEqualTo(existingComputer.getPowerSupply().getEfficiency());

        assertThat(actualComputer.getRams().size())
                .isEqualTo(3);

        // Проверяем, что все нужные методы были вызваны
        verify(computerRepository).existsByNameIgnoreCase(updatedComputer.getName());
        verify(processorService).addProcessor(any(ProcessorDto.class));
        verify(motherboardService).addMotherboard(any(MotherboardDto.class));
        verify(videoCardService).addVideoCard(any(VideoCardDto.class));
        verify(ramService, times(2)).addRam(any(RamDto.class));
        verify(powerSupplyService, times(0)).addPowerSupply(any(PowerSupplyDto.class));
    }

    ComputerDto getComputer() {
        CpuSocketDto socket = new CpuSocketDto();
        socket.setName("LGA1700");
        socket.setManufacturer("Intel");

        ProcessorDto processor = getProcessorDto(socket);

        MotherboardFormFactorDto formFactor = new MotherboardFormFactorDto();
        formFactor.setCode("ATX");
        formFactor.setName("ATX");
        formFactor.setWidthMm(305);
        formFactor.setHeightMm(244);

        MotherboardDto motherboard = new MotherboardDto();
        motherboard.setName("ASUS ROG Strix Z790-E");
        motherboard.setManufacturer("ASUS");
        motherboard.setSocket(socket);
        motherboard.setFormFactor(formFactor);
        motherboard.setChipset("Z790");

        RamDto ram1 = new RamDto();
        ram1.setName("Kingston Fury 32GB DDR5");
        ram1.setType(RamType.DDR5);
        ram1.setFormFactor(RamFormFactor.DIMM);
        ram1.setCapacityMb(32768);
        ram1.setFrequencyMHz(6000);
        ram1.setEcc(false);

        RamDto ram2 = new RamDto();
        ram2.setName("Kingston Fury 32GB DDR5");
        ram2.setType(RamType.DDR5);
        ram2.setFormFactor(RamFormFactor.DIMM);
        ram2.setCapacityMb(32768);
        ram2.setFrequencyMHz(6000);
        ram2.setEcc(false);

        List<RamDto> rams = Arrays.asList(ram1, ram2);

        VideoCardDto videoCard = new VideoCardDto();
        videoCard.setName("NVIDIA RTX 4090");
        videoCard.setManufacturer("NVIDIA");
        videoCard.setTdpWatts(450);

        StorageDeviceDto storage = new StorageDeviceDto();
        storage.setName("Samsung 990 Pro 2TB");
        storage.setFormFactor("2280");
        storage.setConnectionInterface("PCIe");
        storage.setPortType("M.2");
        storage.setCapacityGb(2048);
        storage.setReadSpeedMbps(7450);
        storage.setWriteSpeedMbps(6900);

        List<StorageDeviceDto> storages = List.of(storage);

        PowerSupplyDto powerSupply = new PowerSupplyDto();
        powerSupply.setName("Corsair RM1000e");
        powerSupply.setManufacturer("Corsair");
        powerSupply.setPowerWatts(1000);
        powerSupply.setEfficiency(PsuEfficiency.GOLD);
        powerSupply.setFormFactor(PsuFormFactor.ATX);
        powerSupply.setModular(PsuModular.FULL);

        ComputerDto computerDto = new ComputerDto(processor, motherboard, rams, videoCard, storages, powerSupply);
        computerDto.setName("Asus TF-03-AG");

        return computerDto;
    }

    private static ProcessorDto getProcessorDto(CpuSocketDto socket) {
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
        processor.setTdpWatts(125);
        return processor;
    }
}
