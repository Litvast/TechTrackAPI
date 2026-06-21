package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PrinterService {

    private final PrinterRepository printerRepository;
    private final PrinterMapping printerMapping;

    // CREATE
    @Transactional
    public PrinterDto addPrinter(PrinterDto dto) {
        log.info("=== НАЧАЛО: Добавление принтера ===");
        log.info("Название: {}, Инвентарный номер: {}", dto.getName(), dto.getInventoryNumber());

        if (dto.getId() != null) {
            log.error("Передан ID при создании принтера. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a printer, you must specify a name, not an ID");
        }

        validateAddPrinter(dto);

        if (dto.getStatus() == null) {
            dto.setStatus(EquipmentStatus.IN_STOCK);
            log.debug("Установлен статус по умолчанию: IN_STOCK");
        }

        Printer printer = printerMapping.toEntity(dto);
        printerRepository.save(printer);

        log.info("Принтер создан. ID: {}", printer.getId());
        log.info("=== УСПЕШНО: Принтер добавлен ===");

        return printerMapping.toDto(printer);
    }

    // READ all with pagination
    public Page<PrinterDto> getAllPrinters(Pageable pageable) {
        log.debug("Запрос всех принтеров с пагинацией");

        Page<Printer> printers = printerRepository.findAll(pageable);
        if (printers.isEmpty()) {
            log.warn("Принтеры не найдены");
            throw new NoEntitiesFoundException("No printers found");
        }

        log.debug("Найдено {} принтеров", printers.getTotalElements());
        return printers.map(printerMapping::toDto);
    }

    // READ by id
    public PrinterDto getPrinterById(Long id) {
        log.debug("Поиск принтера по ID: {}", id);

        Printer printer = printerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Принтер с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Printer with id '%d' not found", id)
                    );
                });

        return printerMapping.toDto(printer);
    }

    // READ by name
    public PrinterDto getPrinterByName(String name) {
        log.debug("Поиск принтера по названию: {}", name);

        Printer printer = printerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Принтер с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("Printer with name '%s' not found", name)
                    );
                });

        return printerMapping.toDto(printer);
    }

    // READ by inventory number
    public PrinterDto getPrinterByInventoryNumber(String inventoryNumber) {
        log.debug("Поиск принтера по инвентарному номеру: {}", inventoryNumber);

        Printer printer = printerRepository.findByInventoryNumber(inventoryNumber)
                .orElseThrow(() -> {
                    log.error("Принтер с инвентарным номером '{}' не найден", inventoryNumber);
                    return new EntityNotFoundException(
                            String.format("Printer with inventory number '%s' not found", inventoryNumber)
                    );
                });

        return printerMapping.toDto(printer);
    }

    // COUNT
    public long getCountPrinters() {
        log.debug("Подсчёт общего количества роутеров");

        return printerRepository.count();
    }

    // UPDATE
    @Transactional
    public PrinterDto updatePrinter(Long id, PrinterUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление принтера ===");
        log.info("ID принтера: {}", id);

        Printer existingPrinter = printerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Принтер с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Printer with id '%d' not found", id)
                    );
                });

        if (dto.getName() != null && !existingPrinter.getName().equalsIgnoreCase(dto.getName())) {
            log.info("Изменение названия: {} -> {}", existingPrinter.getName(), dto.getName());

            if (printerRepository.existsByNameIgnoreCase(dto.getName())) {
                log.warn("Принтер с названием '{}' уже существует", dto.getName());
                throw new IllegalArgumentException(
                        String.format("Printer '%s' is already taken", dto.getName())
                );
            }
            existingPrinter.setName(dto.getName());
        }

        if (dto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingPrinter.getManufacturer(), dto.getManufacturer());
            existingPrinter.setManufacturer(dto.getManufacturer());
        }

        if (dto.getInventoryNumber() != null) {
            log.info("Изменение инвентарного номера: {} -> {}", existingPrinter.getInventoryNumber(), dto.getInventoryNumber());
            existingPrinter.setInventoryNumber(dto.getInventoryNumber());
        }

        if (dto.getPrintType() != null) {
            log.info("Изменение типа печати: {} -> {}", existingPrinter.getPrintType(), dto.getPrintType());
            existingPrinter.setPrintType(dto.getPrintType());
        }

        if (dto.getIsColor() != null) {
            log.info("Изменение цветности: {} -> {}", existingPrinter.getIsColor(), dto.getIsColor());
            existingPrinter.setIsColor(dto.getIsColor());
        }

        if (dto.getSpeed() != null) {
            log.info("Изменение скорости: {} -> {} стр/мин", existingPrinter.getSpeed(), dto.getSpeed());
            existingPrinter.setSpeed(dto.getSpeed());
        }

        if (dto.getWidthResolution() != null) {
            log.info("Изменение разрешения по ширине: {} -> {} dpi", existingPrinter.getWidthResolution(), dto.getWidthResolution());
            existingPrinter.setWidthResolution(dto.getWidthResolution());
        }

        if (dto.getHeightResolution() != null) {
            log.info("Изменение разрешения по высоте: {} -> {} dpi", existingPrinter.getHeightResolution(), dto.getHeightResolution());
            existingPrinter.setHeightResolution(dto.getHeightResolution());
        }

        if (dto.getConsumption() != null) {
            log.info("Изменение расходных материалов: {}", dto.getConsumption());
            existingPrinter.setConsumption(dto.getConsumption());
        }

        if (dto.getModel() != null) {
            log.info("Изменение модели: {} -> {}", existingPrinter.getModel(), dto.getModel());
            existingPrinter.setModel(dto.getModel());
        }

        if (dto.getIsDuplex() != null) {
            log.info("Изменение поддержки дуплекса: {} -> {}", existingPrinter.getIsDuplex(), dto.getIsDuplex());
            existingPrinter.setIsDuplex(dto.getIsDuplex());
        }

        if (dto.getIsNetwork() != null) {
            log.info("Изменение сетевой поддержки: {} -> {}", existingPrinter.getIsNetwork(), dto.getIsNetwork());
            existingPrinter.setIsNetwork(dto.getIsNetwork());
        }

        if (dto.getPaperSize() != null) {
            log.info("Изменение формата бумаги: {} -> {}", existingPrinter.getPaperSize(), dto.getPaperSize());
            existingPrinter.setPaperSize(dto.getPaperSize());
        }

        printerRepository.save(existingPrinter);
        log.info("=== УСПЕШНО: Принтер обновлён ===");

        return printerMapping.toDto(existingPrinter);
    }

    // DELETE
    @Transactional
    public void deletePrinter(Long id) {
        log.info("=== НАЧАЛО: Удаление принтера ===");
        log.info("ID принтера: {}", id);

        if (!printerRepository.existsById(id)) {
            log.error("Принтер с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Printer with id '%d' not found", id)
            );
        }

        printerRepository.deleteById(id);
        log.info("=== УСПЕШНО: Принтер удалён ===");
    }

    public void validateAddPrinter(PrinterDto dto) {
        if (printerRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Принтер с названием '{}' уже существует", dto.getName());
            throw new IllegalArgumentException(
                    String.format("Printer '%s' is already taken", dto.getName())
            );
        }
        if (dto.getInventoryNumber() != null && printerRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            log.warn("Принтер с инвентарным номером '{}' уже существует", dto.getInventoryNumber());
            throw new IllegalArgumentException(
                    String.format("Printer with inventory number '%s' already exists", dto.getInventoryNumber())
            );
        }
    }
}