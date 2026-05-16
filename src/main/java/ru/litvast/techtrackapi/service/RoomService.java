package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.RoomDto;
import ru.litvast.techtrackapi.model.dto.RoomUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.RoomMapping;
import ru.litvast.techtrackapi.model.entity.BuildingFloor;
import ru.litvast.techtrackapi.model.entity.Room;
import ru.litvast.techtrackapi.repository.BuildingFloorRepository;
import ru.litvast.techtrackapi.repository.RoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BuildingFloorRepository buildingFloorRepository;
    private final RoomMapping roomMapping;

    @Transactional
    public RoomDto addRoom(RoomDto dto) {
        log.info("=== НАЧАЛО: Добавление комнаты ===");
        log.info("Название: {}, Номер: {}, Этаж ID: {}", dto.getName(), dto.getRoomNumber(), dto.getBuildingFloorId());

        if (dto.getId() != null) {
            log.error("Передан ID при создании комнаты. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a room, you must specify a name, not an ID");
        }

        if (roomRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Комната с названием '{}' уже существует", dto.getName());
            throw new IllegalArgumentException(
                    String.format("Room '%s' is already taken", dto.getName())
            );
        }

        BuildingFloor buildingFloor = buildingFloorRepository.findById(dto.getBuildingFloorId())
                .orElseThrow(() -> {
                    log.error("Этаж с ID {} не найден", dto.getBuildingFloorId());
                    return new EntityNotFoundException(
                            String.format("Building floor with id '%d' not found", dto.getBuildingFloorId())
                    );
                });

        Room room = roomMapping.toEntity(dto);
        room.setBuildingFloor(buildingFloor);
        roomRepository.save(room);

        log.info("Комната создана. ID: {}", room.getId());
        log.info("=== УСПЕШНО: Комната добавлена ===");

        return roomMapping.toDto(room);
    }

    public Page<RoomDto> getAllRooms(Pageable pageable) {
        log.debug("Запрос всех комнат с пагинацией");

        Page<Room> rooms = roomRepository.findAll(pageable);
        if (rooms.isEmpty()) {
            log.warn("Комнаты не найдены");
            throw new NoEntitiesFoundException("No rooms found");
        }

        log.debug("Найдено {} комнат", rooms.getTotalElements());
        return rooms.map(roomMapping::toDto);
    }

    public Page<RoomDto> getRoomsByBuildingFloorId(Long buildingFloorId, Pageable pageable) {
        log.debug("Поиск комнат по этажу ID: {}", buildingFloorId);

        if (!buildingFloorRepository.existsById(buildingFloorId)) {
            log.error("Этаж с ID {} не найден", buildingFloorId);
            throw new EntityNotFoundException(
                    String.format("Building floor with id '%d' not found", buildingFloorId)
            );
        }

        Page<Room> rooms = roomRepository.findByBuildingFloorId(buildingFloorId, pageable);
        if (rooms.isEmpty()) {
            log.warn("Комнаты для этажа ID {} не найдены", buildingFloorId);
            throw new NoEntitiesFoundException("No rooms found for this building floor");
        }

        log.debug("Найдено {} комнат для этажа ID {}", rooms.getTotalElements(), buildingFloorId);
        return rooms.map(roomMapping::toDto);
    }

    public RoomDto getRoomById(Long id) {
        log.debug("Поиск комнаты по ID: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Комната с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Room with id '%d' not found", id)
                    );
                });

        return roomMapping.toDto(room);
    }

    public RoomDto getRoomByName(String name) {
        log.debug("Поиск комнаты по названию: {}", name);

        Room room = roomRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Комната с названием '{}' не найдена", name);
                    return new EntityNotFoundException(
                            String.format("Room with name '%s' not found", name)
                    );
                });

        return roomMapping.toDto(room);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление комнаты ===");
        log.info("ID комнаты: {}", id);

        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Комната с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Room with id '%d' not found", id)
                    );
                });

        if (dto.getName() != null && !existingRoom.getName().equalsIgnoreCase(dto.getName())) {
            log.info("Изменение названия: {} -> {}", existingRoom.getName(), dto.getName());

            if (roomRepository.existsByNameIgnoreCase(dto.getName())) {
                log.warn("Комната с названием '{}' уже существует", dto.getName());
                throw new IllegalArgumentException(
                        String.format("Room '%s' is already taken", dto.getName())
                );
            }
            existingRoom.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            log.info("Изменение описания");
            existingRoom.setDescription(dto.getDescription());
        }

        if (dto.getRoomNumber() != null) {
            log.info("Изменение номера комнаты: {} -> {}", existingRoom.getRoomNumber(), dto.getRoomNumber());
            existingRoom.setRoomNumber(dto.getRoomNumber());
        }

        if (dto.getBuildingFloorId() != null) {
            log.info("Изменение этажа: {} -> {}",
                    existingRoom.getBuildingFloor().getId(), dto.getBuildingFloorId());

            BuildingFloor buildingFloor = buildingFloorRepository.findById(dto.getBuildingFloorId())
                    .orElseThrow(() -> {
                        log.error("Этаж с ID {} не найден", dto.getBuildingFloorId());
                        return new EntityNotFoundException(
                                String.format("Building floor with id '%d' not found", dto.getBuildingFloorId())
                        );
                    });
            existingRoom.setBuildingFloor(buildingFloor);
        }

        roomRepository.save(existingRoom);
        log.info("=== УСПЕШНО: Комната обновлена ===");

        return roomMapping.toDto(existingRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        log.info("=== НАЧАЛО: Удаление комнаты ===");
        log.info("ID комнаты: {}", id);

        if (!roomRepository.existsById(id)) {
            log.error("Комната с ID {} не найдена", id);
            throw new EntityNotFoundException(
                    String.format("Room with id '%d' not found", id)
            );
        }

        roomRepository.deleteById(id);
        log.info("=== УСПЕШНО: Комната удалена ===");
    }
}