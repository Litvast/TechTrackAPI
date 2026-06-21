package ru.litvast.techtrackapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.litvast.techtrackapi.model.dto.equipment.computer.StorageDeviceDto;
import ru.litvast.techtrackapi.service.StorageDeviceService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/storage-device")
@Tag(name = "storage-devices", description = "Методы для работы с накопителями")
public class StorageDeviceController {

    private final StorageDeviceService storageDeviceService;

    @Operation(
            summary = "Добавление одного накопителя (ADMIN)",
            description = "Создаёт новый накопитель с указанными характеристиками. Доступно только для администраторов. Возвращает созданный объект StorageDeviceDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStorageDevice(@Valid @RequestBody StorageDeviceDto storageDeviceDto) {
        StorageDeviceDto created = storageDeviceService.addStorageDevice(storageDeviceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Добавление нескольких накопителей (ADMIN)",
            description = "Создаёт несколько накопителей за один запрос. Принимает список DTO. Доступно только для администраторов. Возвращает список созданных объектов StorageDeviceDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSomeStorageDevices(@Valid @RequestBody List<StorageDeviceDto> storageDeviceDtoList) {
        List<StorageDeviceDto> created = storageDeviceService.addSomeStorageDevices(storageDeviceDtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех накопителей с пагинацией",
            description = "Возвращает страницу со списком всех накопителей с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllStorageDevices(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<StorageDeviceDto> storageDevices = storageDeviceService.getAllStorageDevices(pageable);
        return ResponseEntity.ok(storageDevices);
    }

    @Operation(
            summary = "Поиск накопителя по id",
            description = "Возвращает накопитель по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getStorageDeviceById(@PathVariable Long id) {
        StorageDeviceDto storageDevice = storageDeviceService.getStorageDeviceById(id);
        return ResponseEntity.ok(storageDevice);
    }

    @Operation(
            summary = "Поиск накопителя по названию",
            description = "Возвращает накопитель по его названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getStorageDeviceByName(@PathVariable String name) {
        StorageDeviceDto storageDevice = storageDeviceService.getStorageDeviceByName(name);
        return ResponseEntity.ok(storageDevice);
    }

    @Operation(
            summary = "Обновление накопителя (ADMIN)",
            description = "Обновляет данные существующего накопителя по его id. Доступно только для администраторов. Возвращает обновлённый объект StorageDeviceDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStorageDevice(@PathVariable Long id,
                                                 @Valid @RequestBody StorageDeviceDto storageDeviceDto) {
        StorageDeviceDto updated = storageDeviceService.updateStorageDevice(id, storageDeviceDto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление накопителя (ADMIN)",
            description = "Удаляет накопитель по его id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStorageDevice(@PathVariable Long id) {
        storageDeviceService.deleteStorageDevice(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}