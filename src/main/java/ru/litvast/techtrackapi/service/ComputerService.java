package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.model.dto.equipment.computer.*;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.ComputerMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.Computer;
import ru.litvast.techtrackapi.repository.equipment.computer.ComputerRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ProcessorService processorService;
    private final MotherboardService motherboardService;
    private final RamService ramService;
    private final VideoCardService videoCardService;
    private final PowerSupplyService powerSupplyService;
    private final StorageDeviceService storageDeviceService;
    private final ComputerMapping computerMapping;
    private final ComputerRepository computerRepository;

    @Transactional
    public ComputerDto addComputer(ComputerDto computerDto) {

        // Проверка на то, существует ли компьютер с таким именем
        if (computerRepository.existsByNameIgnoreCase(computerDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Computer '%s' is already taken", computerDto.getName())
            );
        }

        // Проверка, что если комплектующее указано по айди (существует в БД), то оно извлекается из БД и переносится в ДТО,
        // иначе добавляется новое
        if (computerDto.getProcessor().getId() != null) computerDto.setProcessor(processorService.getProcessorById(computerDto.getProcessor().getId()));
            else computerDto.setProcessor(processorService.addProcessor(computerDto.getProcessor()));
        if (computerDto.getMotherboard().getId() != null) computerDto.setMotherboard(motherboardService.getMotherboardById(computerDto.getMotherboard().getId()));
            else computerDto.setMotherboard(motherboardService.addMotherboard(computerDto.getMotherboard()));
        if (computerDto.getVideoCard().getId() != null) computerDto.setVideoCard(videoCardService.getVideoCardById(computerDto.getVideoCard().getId()));
            else computerDto.setVideoCard(videoCardService.addVideoCard(computerDto.getVideoCard()));
        if (computerDto.getPowerSupply().getId() != null) computerDto.setPowerSupply(powerSupplyService.getPowerSupplyById(computerDto.getPowerSupply().getId()));
            else computerDto.setPowerSupply(powerSupplyService.addPowerSupply(computerDto.getPowerSupply()));

        Map<String, Long> ramNameId = new HashMap<>();
        List<RamDto> tempRamList = new ArrayList<>();
        computerDto.getRams().forEach(ram -> {
            RamDto tempRamDto;
            if (ram.getName() != null) {
                if (!ramNameId.containsKey(ram.getName().toLowerCase())) {
                    tempRamDto = ramService.addRam(ram);

                    ramNameId.put(tempRamDto.getName().toLowerCase(), tempRamDto.getId());
                } else {
                    tempRamDto = ramService.getRamById(ramNameId.get(ram.getName().toLowerCase()));
                }
                tempRamList.add(tempRamDto);
            } else {
                tempRamDto = ramService.getRamById(ram.getId());
                tempRamList.add(tempRamDto);
            }
        });

        computerDto.setRams(tempRamList);

        Map<String, Long> storageDeviceNameId = new HashMap<>();
        List<StorageDeviceDto> tempStorageDeviceList = new ArrayList<>();
        computerDto.getStorageDevices().forEach(storageDeviceDto -> {
            StorageDeviceDto tempStorageDeviceDto;
            if (storageDeviceDto.getName() != null) {
                if (!storageDeviceNameId.containsKey(storageDeviceDto.getName().toLowerCase())) {
                    tempStorageDeviceDto = storageDeviceService.addStorageDevice(storageDeviceDto);

                    storageDeviceNameId.put(tempStorageDeviceDto.getName().toLowerCase(), tempStorageDeviceDto.getId());
                } else {
                    tempStorageDeviceDto = storageDeviceService.getStorageDeviceById(storageDeviceNameId.get(storageDeviceDto.getName().toLowerCase()));
                }
                tempStorageDeviceList.add(tempStorageDeviceDto);
            } else {
                tempStorageDeviceDto = storageDeviceService.getStorageDeviceById(storageDeviceDto.getId());
                tempStorageDeviceList.add(tempStorageDeviceDto);
            }
        });

        computerDto.setStorageDevices(tempStorageDeviceList);

        // Проверка совместимости комплектующих между собой по нескольким характеристикам
        checkProcessorAndMotherboardCompatibility(computerDto.getProcessor(), computerDto.getMotherboard());
        checkMotherboardAndRamsCompatibility(computerDto.getMotherboard(), computerDto.getRams());
        checkMotherboardAndStorageDevicesCompatibility(computerDto.getMotherboard(), computerDto.getStorageDevices());
        checkVideoCardAndPowerSupplyCompatibility(computerDto.getVideoCard(), computerDto.getPowerSupply());

        Computer computer = computerMapping.toEntity(computerDto);
        computerRepository.save(computer);
        return computerMapping.toDto(computer);
    }

    private void checkProcessorAndMotherboardCompatibility(ProcessorDto processorDto, MotherboardDto motherboardDto) {
        if ((processorDto.getSocket() != null && motherboardDto.getSocket() != null)
                && !motherboardDto.getSocket().equals(processorDto.getSocket())) {
            throw new IllegalArgumentException("Motherboard and processor sockets are incompatible");
        }
    }

    private void checkMotherboardAndRamsCompatibility(MotherboardDto motherboardDto, List<RamDto> ramDtoList) {
        if (motherboardDto.getMemorySupports() == null) return;

        Map<MemorySupportDto, ArrayList<RamDto>> memoryCheckList = new HashMap<>();
        motherboardDto.getMemorySupports().forEach(memorySupportDto ->
                memoryCheckList.put(memorySupportDto, new ArrayList<>(memorySupportDto.getNumberOfSlots())));

        for (RamDto ramDto : ramDtoList) {
            if (ramDto.getType() == null || ramDto.getFormFactor() == null) continue;

            boolean compatible = false;

            for (MemorySupportDto memorySupportDto : motherboardDto.getMemorySupports()) {
                if (!memorySupportDto.getType().equals(ramDto.getType())) continue;
                if (!memorySupportDto.getFormFactor().equals(ramDto.getFormFactor())) continue;

                if (ramDto.getEcc() != null) {
                    if (ramDto.getEcc() && !Boolean.TRUE.equals(memorySupportDto.getEccSupported())) {
                        throw new IllegalArgumentException("ECC RAM is not supported by this motherboard");
                    }
                    if (!ramDto.getEcc() && !Boolean.TRUE.equals(memorySupportDto.getNonEccSupported())) {
                        throw new IllegalArgumentException("Non-ECC RAM is not supported by this motherboard");
                    }
                }

                memoryCheckList.get(memorySupportDto).add(ramDto);
                compatible = true;
                break;
            }

            if (!compatible) {
                throw new IllegalArgumentException(
                        String.format("RAM '%s' is not compatible with this motherboard", ramDto.getName())
                );
            }
        }

        for (MemorySupportDto memorySupportDto : motherboardDto.getMemorySupports()) {
            if (memorySupportDto.getNumberOfSlots() < memoryCheckList.get(memorySupportDto).size()) {
                throw new IllegalArgumentException(
                        String.format("Amount of RAM '%d' with the type '%s' and form factor '%s' is greater than the number of motherboard slots '%d'",
                                memoryCheckList.get(memorySupportDto).size(),
                                memorySupportDto.getType(),
                                memorySupportDto.getFormFactor(),
                                memorySupportDto.getNumberOfSlots())
                );
            }

            long sumRamVolumes = memoryCheckList.get(memorySupportDto).stream()
                    .mapToLong(RamDto::getCapacityMb)
                    .sum() / 1024;

            if (memorySupportDto.getMaxMemoryGb() < sumRamVolumes) {
                throw new IllegalArgumentException(
                        String.format("The amount of RAM memory '%d' GB with type '%s' and form factor '%s' is greater than the supported amount on the motherboard '%d' GB",
                                sumRamVolumes,
                                memorySupportDto.getType(),
                                memorySupportDto.getFormFactor(),
                                memorySupportDto.getMaxMemoryGb())
                );
            }
        }
    }

    private void checkMotherboardAndStorageDevicesCompatibility(MotherboardDto motherboardDto, List<StorageDeviceDto> storageDeviceDtoList) {
        if (motherboardDto.getStoragePorts() == null) return;

        Map<StoragePortDto, ArrayList<StorageDeviceDto>> storageCheckList = new HashMap<>();
        motherboardDto.getStoragePorts().forEach(storagePortDto ->
                storageCheckList.put(storagePortDto, new ArrayList<>(storageDeviceDtoList.size())));

        for (StorageDeviceDto storageDto : storageDeviceDtoList) {
            if (storageDto.getConnectionInterface() == null || storageDto.getPortType() == null || storageDto.getFormFactor() == null) continue;

            boolean compatible = false;

            for (StoragePortDto storagePortDto : motherboardDto.getStoragePorts()) {
                if (!storagePortDto.getConnectionInterface().equalsIgnoreCase(storageDto.getConnectionInterface())) continue;
                if (!storagePortDto.getPortType().equalsIgnoreCase(storageDto.getPortType())) continue;
                if (!storagePortDto.getFormFactor().equalsIgnoreCase(storageDto.getFormFactor())) continue;

                storageCheckList.get(storagePortDto).add(storageDto);
                compatible = true;
                break;
            }

            if (!compatible) {
                throw new IllegalArgumentException(
                        String.format("Storage '%s' is not compatible with this motherboard", storageDto.getName())
                );
            }
        }

        for (StoragePortDto storagePortDto : motherboardDto.getStoragePorts()) {
            if (storagePortDto.getCount() < storageCheckList.get(storagePortDto).size()) {
                throw new IllegalArgumentException(
                        String.format("The number of drives '%d' with connection type '%s', connection interface '%s', and form factor '%s' is greater than the number of motherboard slots '%d'",
                                storageCheckList.get(storagePortDto).size(),
                                storagePortDto.getPortType(),
                                storagePortDto.getConnectionInterface(),
                                storagePortDto.getFormFactor(),
                                storagePortDto.getCount())
                );
            }
        }
    }

    private void checkVideoCardAndPowerSupplyCompatibility(VideoCardDto videoCardDto, PowerSupplyDto powerSupplyDto) {
        if (videoCardDto.getTdpWatts() == null || powerSupplyDto.getPowerWatts() == null) return;

        if (videoCardDto.getTdpWatts() > powerSupplyDto.getPowerWatts() * 0.7) {
            throw new IllegalArgumentException("The video card's power should not exceed 70 percent of the power supply's capacity.");
        }
    }
}
