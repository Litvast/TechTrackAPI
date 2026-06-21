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
import ru.litvast.techtrackapi.model.dto.equipment.RouterDto;
import ru.litvast.techtrackapi.model.dto.equipment.RouterUpdateDto;
import ru.litvast.techtrackapi.service.RouterService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/equipment/router")
@Tag(name = "routers", description = "Методы для работы с роутерами")
public class RouterController {

    private final RouterService routerService;

    @Operation(
            summary = "Добавление роутера (ADMIN)",
            description = "Создаёт новый роутер с указанными параметрами. Доступно только для администраторов. Возвращает созданный объект RouterDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRouter(@Valid @RequestBody RouterDto dto) {
        RouterDto created = routerService.addRouter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Получение всех роутеров с пагинацией",
            description = "Возвращает страницу со списком всех роутеров с поддержкой пагинации и сортировки по названию.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<?> getAllRouters(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<RouterDto> routers = routerService.getAllRouters(pageable);
        return ResponseEntity.ok(routers);
    }

    @Operation(
            summary = "Поиск роутера по id",
            description = "Возвращает роутер по его идентификатору.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRouterById(@PathVariable Long id) {
        RouterDto router = routerService.getRouterById(id);
        return ResponseEntity.ok(router);
    }

    @Operation(
            summary = "Поиск роутера по названию",
            description = "Возвращает роутер по его названию (регистронезависимый поиск).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-name/{name}")
    public ResponseEntity<?> getRouterByName(@PathVariable String name) {
        RouterDto router = routerService.getRouterByName(name);
        return ResponseEntity.ok(router);
    }

    @Operation(
            summary = "Поиск роутера по инвентарному номеру",
            description = "Возвращает роутер по его уникальному инвентарному номеру.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/by-inventory/{inventoryNumber}")
    public ResponseEntity<?> getRouterByInventoryNumber(@PathVariable String inventoryNumber) {
        RouterDto router = routerService.getRouterByInventoryNumber(inventoryNumber);
        return ResponseEntity.ok(router);
    }

    @Operation(
            summary = "Подсчёт общего количества роутеров",
            description = "Возвращает общее количество роутеров в системе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/count")
    public ResponseEntity<?> getCountRouters() {
        long countRouters = routerService.getCountRouters();
        return ResponseEntity.ok(countRouters);
    }

    @Operation(
            summary = "Обновление роутера (ADMIN)",
            description = "Обновляет данные существующего роутера по его id. Доступно только для администраторов. Возвращает обновлённый объект RouterDto.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRouter(@PathVariable Long id,
                                          @Valid @RequestBody RouterUpdateDto dto) {
        RouterDto updated = routerService.updateRouter(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удаление роутера (ADMIN)",
            description = "Удаляет роутер по его id. Доступно только для администраторов. Возвращает сообщение об успешном удалении.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRouter(@PathVariable Long id) {
        routerService.deleteRouter(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}