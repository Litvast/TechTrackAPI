package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BuildingFloorRepository buildingFloorRepository;
    private final RoomMapping roomMapping;

    @Transactional
    public RoomDto addRoom(RoomDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a room, you must specify a name, not an ID");
        }

        if (roomRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Room '%s' is already taken", dto.getName())
            );
        }

        BuildingFloor buildingFloor = buildingFloorRepository.findById(dto.getBuildingFloorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Building floor with id '%d' not found", dto.getBuildingFloorId())
                ));

        Room room = roomMapping.toEntity(dto);
        room.setBuildingFloor(buildingFloor);
        roomRepository.save(room);
        return roomMapping.toDto(room);
    }

    public Page<RoomDto> getAllRooms(Pageable pageable) {
        Page<Room> rooms = roomRepository.findAll(pageable);
        if (rooms.isEmpty()) {
            throw new NoEntitiesFoundException("No rooms found");
        }
        return rooms.map(roomMapping::toDto);
    }

    public Page<RoomDto> getRoomsByBuildingFloorId(Long buildingFloorId, Pageable pageable) {
        if (!buildingFloorRepository.existsById(buildingFloorId)) {
            throw new EntityNotFoundException(
                    String.format("Building floor with id '%d' not found", buildingFloorId)
            );
        }

        Page<Room> rooms = roomRepository.findByBuildingFloorId(buildingFloorId, pageable);
        if (rooms.isEmpty()) {
            throw new NoEntitiesFoundException("No rooms found for this building floor");
        }
        return rooms.map(roomMapping::toDto);
    }

    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Room with id '%d' not found", id)
                ));
        return roomMapping.toDto(room);
    }

    public RoomDto getRoomByName(String name) {
        Room room = roomRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Room with name '%s' not found", name)
                ));
        return roomMapping.toDto(room);
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomUpdateDto dto) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Room with id '%d' not found", id)
                ));

        if (dto.getName() != null && !existingRoom.getName().equalsIgnoreCase(dto.getName())) {
            if (roomRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Room '%s' is already taken", dto.getName())
                );
            }
            existingRoom.setName(dto.getName());
        }

        if (dto.getDescription() != null) existingRoom.setDescription(dto.getDescription());
        if (dto.getRoomNumber() != null) existingRoom.setRoomNumber(dto.getRoomNumber());

        if (dto.getBuildingFloorId() != null) {
            BuildingFloor buildingFloor = buildingFloorRepository.findById(dto.getBuildingFloorId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Building floor with id '%d' not found", dto.getBuildingFloorId())
                    ));
            existingRoom.setBuildingFloor(buildingFloor);
        }

        roomRepository.save(existingRoom);
        return roomMapping.toDto(existingRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Room with id '%d' not found", id)
            );
        }
        roomRepository.deleteById(id);
    }
}