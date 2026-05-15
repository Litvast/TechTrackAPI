package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.PrinterDto;
import ru.litvast.techtrackapi.model.dto.equipment.PrinterUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.PrinterMapping;
import ru.litvast.techtrackapi.model.entity.equipment.EquipmentStatus;
import ru.litvast.techtrackapi.model.entity.equipment.Printer;
import ru.litvast.techtrackapi.repository.equipment.PrinterRepository;

@Service
@RequiredArgsConstructor
public class PrinterService {

    private final PrinterRepository printerRepository;
    private final PrinterMapping printerMapping;

    @Transactional
    public PrinterDto addPrinter(PrinterDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a printer, you must specify a name, not an ID");
        }

        validateAddPrinter(dto);

        if (dto.getStatus() == null) {
            dto.setStatus(EquipmentStatus.IN_STOCK);
        }

        Printer printer = printerMapping.toEntity(dto);
        printerRepository.save(printer);
        return printerMapping.toDto(printer);
    }

    public Page<PrinterDto> getAllPrinters(Pageable pageable) {
        Page<Printer> printers = printerRepository.findAll(pageable);
        if (printers.isEmpty()) {
            throw new NoEntitiesFoundException("No printers found");
        }
        return printers.map(printerMapping::toDto);
    }

    public PrinterDto getPrinterById(Long id) {
        Printer printer = printerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Printer with id '%d' not found", id)
                ));
        return printerMapping.toDto(printer);
    }

    public PrinterDto getPrinterByName(String name) {
        Printer printer = printerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Printer with name '%s' not found", name)
                ));
        return printerMapping.toDto(printer);
    }

    public PrinterDto getPrinterByInventoryNumber(String inventoryNumber) {
        Printer printer = printerRepository.findByInventoryNumber(inventoryNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Printer with inventory number '%s' not found", inventoryNumber)
                ));
        return printerMapping.toDto(printer);
    }

    @Transactional
    public PrinterDto updatePrinter(Long id, PrinterUpdateDto dto) {
        Printer existingPrinter = printerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Printer with id '%d' not found", id)
                ));

        if (dto.getName() != null && !existingPrinter.getName().equalsIgnoreCase(dto.getName())) {
            if (printerRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Printer '%s' is already taken", dto.getName())
                );
            }
            existingPrinter.setName(dto.getName());
        }

        if (dto.getManufacturer() != null) existingPrinter.setManufacturer(dto.getManufacturer());
        if (dto.getInventoryNumber() != null) existingPrinter.setInventoryNumber(dto.getInventoryNumber());
        if (dto.getPrintType() != null) existingPrinter.setPrintType(dto.getPrintType());
        if (dto.getIsColor() != null) existingPrinter.setIsColor(dto.getIsColor());
        if (dto.getSpeed() != null) existingPrinter.setSpeed(dto.getSpeed());
        if (dto.getWidthResolution() != null) existingPrinter.setWidthResolution(dto.getWidthResolution());
        if (dto.getHeightResolution() != null) existingPrinter.setHeightResolution(dto.getHeightResolution());
        if (dto.getConsumption() != null) existingPrinter.setConsumption(dto.getConsumption());
        if (dto.getModel() != null) existingPrinter.setModel(dto.getModel());
        if (dto.getIsDuplex() != null) existingPrinter.setIsDuplex(dto.getIsDuplex());
        if (dto.getIsNetwork() != null) existingPrinter.setIsNetwork(dto.getIsNetwork());
        if (dto.getPaperSize() != null) existingPrinter.setPaperSize(dto.getPaperSize());

        printerRepository.save(existingPrinter);
        return printerMapping.toDto(existingPrinter);
    }

    @Transactional
    public void deletePrinter(Long id) {
        if (!printerRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Printer with id '%d' not found", id)
            );
        }
        printerRepository.deleteById(id);
    }

    public void validateAddPrinter(PrinterDto dto) {
        if (printerRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Printer '%s' is already taken", dto.getName())
            );
        }
        if (dto.getInventoryNumber() != null && printerRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            throw new IllegalArgumentException(
                    String.format("Printer with inventory number '%s' already exists", dto.getInventoryNumber())
            );
        }
    }
}