package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.RouterDto;
import ru.litvast.techtrackapi.model.dto.equipment.RouterUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.RouterMapping;
import ru.litvast.techtrackapi.model.entity.equipment.Router;
import ru.litvast.techtrackapi.repository.equipment.RouterRepository;

@Service
@RequiredArgsConstructor
public class RouterService {

    private final RouterRepository routerRepository;
    private final RouterMapping routerMapping;

    @Transactional
    public RouterDto addRouter(RouterDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a router, you must specify a name, not an ID");
        }

        validateAddRouter(dto);

        Router router = routerMapping.toEntity(dto);
        routerRepository.save(router);
        return routerMapping.toDto(router);
    }

    public Page<RouterDto> getAllRouters(Pageable pageable) {
        Page<Router> routers = routerRepository.findAll(pageable);
        if (routers.isEmpty()) {
            throw new NoEntitiesFoundException("No routers found");
        }
        return routers.map(routerMapping::toDto);
    }

    public RouterDto getRouterById(Long id) {
        Router router = routerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Router with id '%d' not found", id)
                ));
        return routerMapping.toDto(router);
    }

    public RouterDto getRouterByName(String name) {
        Router router = routerRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Router with name '%s' not found", name)
                ));
        return routerMapping.toDto(router);
    }

    public RouterDto getRouterByInventoryNumber(String inventoryNumber) {
        Router router = routerRepository.findByInventoryNumber(inventoryNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Router with inventory number '%s' not found", inventoryNumber)
                ));
        return routerMapping.toDto(router);
    }

    @Transactional
    public RouterDto updateRouter(Long id, RouterUpdateDto dto) {
        Router existingRouter = routerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Router with id '%d' not found", id)
                ));

        if (dto.getName() != null && !existingRouter.getName().equalsIgnoreCase(dto.getName())) {
            if (routerRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Router '%s' is already taken", dto.getName())
                );
            }
            existingRouter.setName(dto.getName());
        }

        if (dto.getManufacturer() != null) existingRouter.setManufacturer(dto.getManufacturer());
        if (dto.getInventoryNumber() != null) existingRouter.setInventoryNumber(dto.getInventoryNumber());
        if (dto.getWanBandwidth() != null) existingRouter.setWanBandwidth(dto.getWanBandwidth());
        if (dto.getLanBandwidth() != null) existingRouter.setLanBandwidth(dto.getLanBandwidth());
        if (dto.getWanPorts() != null) existingRouter.setWanPorts(dto.getWanPorts());
        if (dto.getLanPorts() != null) existingRouter.setLanPorts(dto.getLanPorts());
        if (dto.getUsbPorts() != null) existingRouter.setUsbPorts(dto.getUsbPorts());
        if (dto.getBand() != null) existingRouter.setBand(dto.getBand());
        if (dto.getSecurityStandard() != null) existingRouter.setSecurityStandard(dto.getSecurityStandard());

        routerRepository.save(existingRouter);
        return routerMapping.toDto(existingRouter);
    }

    @Transactional
    public void deleteRouter(Long id) {
        if (!routerRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Router with id '%d' not found", id)
            );
        }
        routerRepository.deleteById(id);
    }

    public void validateAddRouter(RouterDto dto) {
        if (routerRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Router '%s' is already taken", dto.getName())
            );
        }
        if (dto.getInventoryNumber() != null && routerRepository.existsByInventoryNumber(dto.getInventoryNumber())) {
            throw new IllegalArgumentException(
                    String.format("Router with inventory number '%s' already exists", dto.getInventoryNumber())
            );
        }
    }
}