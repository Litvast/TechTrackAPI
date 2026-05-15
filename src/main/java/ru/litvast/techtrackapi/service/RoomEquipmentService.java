package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.mapping.RoomEquipmentMapping;
import ru.litvast.techtrackapi.model.dto.RoomEquipmentDto;
import ru.litvast.techtrackapi.model.dto.RoomEquipmentUpdateDto;
import ru.litvast.techtrackapi.model.entity.Room;
import ru.litvast.techtrackapi.model.entity.RoomEquipment;
import ru.litvast.techtrackapi.model.entity.equipment.Equipment;
import ru.litvast.techtrackapi.repository.RoomEquipmentRepository;
import ru.litvast.techtrackapi.repository.RoomRepository;
import ru.litvast.techtrackapi.repository.equipment.EquipmentRepository;

@Service
@RequiredArgsConstructor
public class RoomEquipmentService {

    private final RoomEquipmentRepository roomEquipmentRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final RoomEquipmentMapping roomEquipmentMapping;

    @Transactional
    public RoomEquipmentDto addRoomEquipment(RoomEquipmentDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a room equipment record, you must specify quantity, not an ID");
        }

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Room with id '%d' not found", dto.getRoomId())
                ));

        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Equipment with id '%d' not found", dto.getEquipmentId())
                ));

        if (roomEquipmentRepository.existsByRoomIdAndEquipmentId(dto.getRoomId(), dto.getEquipmentId())) {
            throw new IllegalArgumentException(
                    String.format("Equipment with id '%d' already exists in room with id '%d'",
                            dto.getEquipmentId(), dto.getRoomId())
            );
        }

        RoomEquipment roomEquipment = roomEquipmentMapping.toEntity(dto);
        roomEquipment.setRoom(room);
        roomEquipment.setEquipment(equipment);
        roomEquipmentRepository.save(roomEquipment);
        return roomEquipmentMapping.toDto(roomEquipment);
    }

    public Page<RoomEquipmentDto> getAllRoomEquipments(Pageable pageable) {
        Page<RoomEquipment> items = roomEquipmentRepository.findAll(pageable);
        if (items.isEmpty()) {
            throw new NoEntitiesFoundException("No room equipment records found");
        }
        return items.map(roomEquipmentMapping::toDto);
    }

    public Page<RoomEquipmentDto> getRoomEquipmentsByRoomId(Long roomId, Pageable pageable) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException(
                    String.format("Room with id '%d' not found", roomId)
            );
        }

        Page<RoomEquipment> items = roomEquipmentRepository.findByRoomId(roomId, pageable);
        if (items.isEmpty()) {
            throw new NoEntitiesFoundException("No equipment found in this room");
        }
        return items.map(roomEquipmentMapping::toDto);
    }

    public Page<RoomEquipmentDto> getRoomEquipmentsByEquipmentId(Long equipmentId, Pageable pageable) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new EntityNotFoundException(
                    String.format("Equipment with id '%d' not found", equipmentId)
            );
        }

        Page<RoomEquipment> items = roomEquipmentRepository.findByEquipmentId(equipmentId, pageable);
        if (items.isEmpty()) {
            throw new NoEntitiesFoundException("This equipment is not assigned to any room");
        }
        return items.map(roomEquipmentMapping::toDto);
    }

    public RoomEquipmentDto getRoomEquipmentById(Long id) {
        RoomEquipment item = roomEquipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Room equipment record with id '%d' not found", id)
                ));
        return roomEquipmentMapping.toDto(item);
    }

    @Transactional
    public RoomEquipmentDto updateRoomEquipment(Long id, RoomEquipmentUpdateDto dto) {
        RoomEquipment existingItem = roomEquipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Room equipment record with id '%d' not found", id)
                ));

        if (dto.getQuantity() != null) {
            existingItem.setQuantity(dto.getQuantity());
        }

        if (dto.getRoomId() != null && !existingItem.getRoom().getId().equals(dto.getRoomId())) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Room with id '%d' not found", dto.getRoomId())
                    ));
            existingItem.setRoom(room);
        }

        if (dto.getEquipmentId() != null && !existingItem.getEquipment().getId().equals(dto.getEquipmentId())) {
            Long newRoomId = dto.getRoomId() != null ? dto.getRoomId() : existingItem.getRoom().getId();
            if (roomEquipmentRepository.existsByRoomIdAndEquipmentId(newRoomId, dto.getEquipmentId())) {
                throw new IllegalArgumentException(
                        String.format("Equipment with id '%d' already exists in this room", dto.getEquipmentId())
                );
            }

            Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Equipment with id '%d' not found", dto.getEquipmentId())
                    ));
            existingItem.setEquipment(equipment);
        }

        roomEquipmentRepository.save(existingItem);
        return roomEquipmentMapping.toDto(existingItem);
    }

    @Transactional
    public void deleteRoomEquipment(Long id) {
        if (!roomEquipmentRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Room equipment record with id '%d' not found", id)
            );
        }
        roomEquipmentRepository.deleteById(id);
    }
}