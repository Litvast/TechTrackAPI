package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.RouterDto;
import ru.litvast.techtrackapi.model.dto.equipment.RouterUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.RouterMapping;
import ru.litvast.techtrackapi.model.entity.equipment.EquipmentStatus;
import ru.litvast.techtrackapi.model.entity.equipment.Router;
import ru.litvast.techtrackapi.repository.equipment.RouterRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouterService {

    private final RouterRepository routerRepository;
    private final RouterMapping routerMapping;

    // CREATE
    @Transactional
    public RouterDto addRouter(RouterDto dto) {
        log.info("=== НАЧАЛО: Добавление роутера ===");
        log.info("Название: {}, Инвентарный номер: {}", dto.getName(), dto.getInventoryNumber());

        if (dto.getId() != null) {
            log.error("Передан ID при создании роутера. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a router, you must specify a name, not an ID");
        }

        validateAddRouter(dto);

        if (dto.getStatus() == null) {
            dto.setStatus(EquipmentStatus.IN_STOCK);
            log.debug("Установлен статус по умолчанию: IN_STOCK");
        }

        Router router = routerMapping.toEntity(dto);
        routerRepository.save(router);

        log.info("Роутер создан. ID: {}", router.getId());
        log.info("=== УСПЕШНО: Роутер добавлен ===");

        return routerMapping.toDto(router);
    }

    // READ all with pagination
    public Page<RouterDto> getAllRouters(Pageable pageable) {
        log.debug("Запрос всех роутеров с пагинацией");

        Page<Router> routers = routerRepository.findAll(pageable);
        if (routers.isEmpty()) {
            log.warn("Роутеры не найдены");
            throw new NoEntitiesFoundException("No routers found");
        }

        log.debug("Найдено {} роутеров", routers.getTotalElements());
        return routers.map(routerMapping::toDto);
    }

    // READ by id
    public RouterDto getRouterById(Long id) {
        log.debug("Поиск роутера по ID: {}", id);

        Router router = routerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Роутер с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Router with id '%d' not found", id)
                    );
                });

        return routerMapping.toDto(router);
    }

    // READ by name
    public RouterDto getRouterByName(String name) {
        log.debug("Поиск роутера по названию: {}", name);

        Router router = routerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Роутер с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("Router with name '%s' not found", name)
                    );
                });

        return routerMapping.toDto(router);
    }

    // READ by inventory number
    public RouterDto getRouterByInventoryNumber(String inventoryNumber) {
        log.debug("Поиск роутера по инвентарному номеру: {}", inventoryNumber);

        Router router = routerRepository.findByInventoryNumber(inventoryNumber)
                .orElseThrow(() -> {
                    log.error("Роутер с инвентарным номером '{}' не найден", inventoryNumber);
                    return new EntityNotFoundException(
                            String.format("Router with inventory number '%s' not found", inventoryNumber)
                    );
                });

        return routerMapping.toDto(router);
    }

    // COUNT
    public long getCountRouters() {
        log.debug("Подсчёт общего количества роутеров");

        return routerRepository.count();
    }

    // UPDATE
    @Transactional
    public RouterDto updateRouter(Long id, RouterUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление роутера ===");
        log.info("ID роутера: {}", id);

        Router existingRouter = routerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Роутер с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("Router with id '%d' not found", id)
                    );
                });

        if (dto.getName() != null && !existingRouter.getName().equalsIgnoreCase(dto.getName())) {
            log.info("Изменение названия: {} -> {}", existingRouter.getName(), dto.getName());

            if (routerRepository.existsByNameIgnoreCase(dto.getName())) {
                log.warn("Роутер с названием '{}' уже существует", dto.getName());
                throw new IllegalArgumentException(
                        String.format("Router '%s' is already taken", dto.getName())
                );
            }
            existingRouter.setName(dto.getName());
        }

        if (dto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingRouter.getManufacturer(), dto.getManufacturer());
            existingRouter.setManufacturer(dto.getManufacturer());
        }

        if (dto.getInventoryNumber() != null) {
            log.info("Изменение инвентарного номера: {} -> {}", existingRouter.getInventoryNumber(), dto.getInventoryNumber());
            existingRouter.setInventoryNumber(dto.getInventoryNumber());
        }

        if (dto.getWanBandwidth() != null) {
            log.info("Изменение WAN пропускной способности: {} -> {} Мбит/с",
                    existingRouter.getWanBandwidth(), dto.getWanBandwidth());
            existingRouter.setWanBandwidth(dto.getWanBandwidth());
        }

        if (dto.getLanBandwidth() != null) {
            log.info("Изменение LAN пропускной способности: {} -> {} Мбит/с",
                    existingRouter.getLanBandwidth(), dto.getLanBandwidth());
            existingRouter.setLanBandwidth(dto.getLanBandwidth());
        }

        if (dto.getWanPorts() != null) {
            log.info("Изменение количества WAN портов: {} -> {}", existingRouter.getWanPorts(), dto.getWanPorts());
            existingRouter.setWanPorts(dto.getWanPorts());
        }

        if (dto.getLanPorts() != null) {
            log.info("Изменение количества LAN портов: {} -> {}", existingRouter.getLanPorts(), dto.getLanPorts());
            existingRouter.setLanPorts(dto.getLanPorts());
        }

        if (dto.getUsbPorts() != null) {
            log.info("Изменение количества USB портов: {} -> {}", existingRouter.getUsbPorts(), dto.getUsbPorts());
            existingRouter.setUsbPorts(dto.getUsbPorts());
        }

        if (dto.getBand() != null) {
            log.info("Изменение диапазона частот: {} -> {}", existingRouter.getBand(), dto.getBand());
            existingRouter.setBand(dto.getBand());
        }

        if (dto.getSecurityStandard() != null) {
            log.info("Изменение стандарта безопасности: {} -> {}", existingRouter.getSecurityStandard(), dto.getSecurityStandard());
            existingRouter.setSecurityStandard(dto.getSecurityStandard());
        }

        routerRepository.save(existingRouter);
        log.info("=== УСПЕШНО: Роутер обновлён ===");

        return routerMapping.toDto(existingRouter);
    }

    // DELETE
    @Transactional
    public void deleteRouter(Long id) {
        log.info("=== НАЧАЛО: Удаление роутера ===");
        log.info("ID роутера: {}", id);

        if (!routerRepository.existsById(id)) {
            log.error("Роутер с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("Router with id '%d' not found", id)
            );
        }

        routerRepository.deleteById(id);
        log.info("=== УСПЕШНО: Роутер удалён ===");
    }

    public void validateAddRouter(RouterDto dto) {
        if (routerRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Роутер с названием '{}' уже существует", dto.getName());
            throw new IllegalArgumentException(
                    String.format("Router '%s' is already taken", dto.getName())
            );
        }
        if (dto.getInventoryNumber() != null && routerRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            log.warn("Роутер с инвентарным номером '{}' уже существует", dto.getInventoryNumber());
            throw new IllegalArgumentException(
                    String.format("Router with inventory number '%s' already exists", dto.getInventoryNumber())
            );
        }
    }
}