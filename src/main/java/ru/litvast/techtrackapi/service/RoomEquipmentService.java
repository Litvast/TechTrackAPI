package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomEquipmentService {

    private final RoomEquipmentRepository roomEquipmentRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final RoomEquipmentMapping roomEquipmentMapping;

    @Transactional
    public RoomEquipmentDto addRoomEquipment(RoomEquipmentDto dto) {
        log.info("=== НАЧАЛО: Добавление оборудования в комнату ===");
        log.info("Комната ID: {}, Оборудование ID: {}, Количество: {}",
                dto.getRoomId(), dto.getEquipmentId(), dto.getQuantity());

        if (dto.getId() != null) {
            log.error("Передан ID при создании записи. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a room equipment record, you must specify quantity, not an ID");
        }

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> {
                    log.error("Комната с ID {} не найдена", dto.getRoomId());
                    return new EntityNotFoundException(
                            String.format("Room with id '%d' not found", dto.getRoomId())
                    );
                });

        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> {
                    log.error("Оборудование с ID {} не найдено", dto.getEquipmentId());
                    return new EntityNotFoundException(
                            String.format("Equipment with id '%d' not found", dto.getEquipmentId())
                    );
                });

        if (roomEquipmentRepository.existsByRoomIdAndEquipmentId(dto.getRoomId(), dto.getEquipmentId())) {
            log.warn("Оборудование ID {} уже существует в комнате ID {}", dto.getEquipmentId(), dto.getRoomId());
            throw new IllegalArgumentException(
                    String.format("Equipment with id '%d' already exists in room with id '%d'",
                            dto.getEquipmentId(), dto.getRoomId())
            );
        }

        RoomEquipment roomEquipment = roomEquipmentMapping.toEntity(dto);
        roomEquipment.setRoom(room);
        roomEquipment.setEquipment(equipment);
        roomEquipmentRepository.save(roomEquipment);

        log.info("Оборудование добавлено в комнату. ID записи: {}", roomEquipment.getId());
        log.info("=== УСПЕШНО: Оборудование добавлено в комнату ===");

        return roomEquipmentMapping.toDto(roomEquipment);
    }

    public Page<RoomEquipmentDto> getAllRoomEquipments(Pageable pageable) {
        log.debug("Запрос всех записей оборудования в комнатах с пагинацией");

        Page<RoomEquipment> items = roomEquipmentRepository.findAll(pageable);
        if (items.isEmpty()) {
            log.warn("Записи оборудования в комнатах не найдены");
            throw new NoEntitiesFoundException("No room equipment records found");
        }

        log.debug("Найдено {} записей", items.getTotalElements());
        return items.map(roomEquipmentMapping::toDto);
    }

    public Page<RoomEquipmentDto> getRoomEquipmentsByRoomId(Long roomId, Pageable pageable) {
        log.debug("Поиск оборудования в комнате ID: {}", roomId);

        if (!roomRepository.existsById(roomId)) {
            log.error("Комната с ID {} не найдена", roomId);
            throw new EntityNotFoundException(
                    String.format("Room with id '%d' not found", roomId)
            );
        }

        Page<RoomEquipment> items = roomEquipmentRepository.findByRoomId(roomId, pageable);
        if (items.isEmpty()) {
            log.warn("Оборудование в комнате ID {} не найдено", roomId);
            throw new NoEntitiesFoundException("No equipment found in this room");
        }

        log.debug("Найдено {} единиц оборудования в комнате ID {}", items.getTotalElements(), roomId);
        return items.map(roomEquipmentMapping::toDto);
    }

    public Page<RoomEquipmentDto> getRoomEquipmentsByEquipmentId(Long equipmentId, Pageable pageable) {
        log.debug("Поиск комнат с оборудованием ID: {}", equipmentId);

        if (!equipmentRepository.existsById(equipmentId)) {
            log.error("Оборудование с ID {} не найдено", equipmentId);
            throw new EntityNotFoundException(
                    String.format("Equipment with id '%d' not found", equipmentId)
            );
        }

        Page<RoomEquipment> items = roomEquipmentRepository.findByEquipmentId(equipmentId, pageable);
        if (items.isEmpty()) {
            log.warn("Оборудование ID {} не назначено ни в одну комнату", equipmentId);
            throw new NoEntitiesFoundException("This equipment is not assigned to any room");
        }

        log.debug("Оборудование ID {} найдено в {} комнатах", equipmentId, items.getTotalElements());
        return items.map(roomEquipmentMapping::toDto);
    }

    public RoomEquipmentDto getRoomEquipmentById(Long id) {
        log.debug("Поиск записи по ID: {}", id);

        RoomEquipment item = roomEquipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Запись с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Room equipment record with id '%d' not found", id)
                    );
                });

        return roomEquipmentMapping.toDto(item);
    }

    public long getCountRoomEquipments() {
        log.debug("Подсчёт общего количества записей об оборудовании в комнатах");
        return roomEquipmentRepository.count();
    }

    @Transactional
    public RoomEquipmentDto updateRoomEquipment(Long id, RoomEquipmentUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление записи оборудования в комнате ===");
        log.info("ID записи: {}", id);

        RoomEquipment existingItem = roomEquipmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Запись с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Room equipment record with id '%d' not found", id)
                    );
                });

        if (dto.getQuantity() != null) {
            log.info("Изменение количества: {} -> {}", existingItem.getQuantity(), dto.getQuantity());
            existingItem.setQuantity(dto.getQuantity());
        }

        if (dto.getRoomId() != null && !existingItem.getRoom().getId().equals(dto.getRoomId())) {
            log.info("Изменение комнаты: {} -> {}", existingItem.getRoom().getId(), dto.getRoomId());

            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> {
                        log.error("Комната с ID {} не найдена", dto.getRoomId());
                        return new EntityNotFoundException(
                                String.format("Room with id '%d' not found", dto.getRoomId())
                        );
                    });
            existingItem.setRoom(room);
        }

        if (dto.getEquipmentId() != null && !existingItem.getEquipment().getId().equals(dto.getEquipmentId())) {
            Long newRoomId = dto.getRoomId() != null ? dto.getRoomId() : existingItem.getRoom().getId();

            if (roomEquipmentRepository.existsByRoomIdAndEquipmentId(newRoomId, dto.getEquipmentId())) {
                log.warn("Оборудование ID {} уже существует в комнате ID {}", dto.getEquipmentId(), newRoomId);
                throw new IllegalArgumentException(
                        String.format("Equipment with id '%d' already exists in this room", dto.getEquipmentId())
                );
            }

            log.info("Изменение оборудования: {} -> {}", existingItem.getEquipment().getId(), dto.getEquipmentId());

            Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                    .orElseThrow(() -> {
                        log.error("Оборудование с ID {} не найдено", dto.getEquipmentId());
                        return new EntityNotFoundException(
                                String.format("Equipment with id '%d' not found", dto.getEquipmentId())
                        );
                    });
            existingItem.setEquipment(equipment);
        }

        roomEquipmentRepository.save(existingItem);
        log.info("=== УСПЕШНО: Запись обновлена ===");

        return roomEquipmentMapping.toDto(existingItem);
    }

    @Transactional
    public void deleteRoomEquipment(Long id) {
        log.info("=== НАЧАЛО: Удаление записи оборудования из комнаты ===");
        log.info("ID записи: {}", id);

        if (!roomEquipmentRepository.existsById(id)) {
            log.error("Запись с ID {} не найдена", id);
            throw new EntityNotFoundException(
                    String.format("Room equipment record with id '%d' not found", id)
            );
        }

        roomEquipmentRepository.deleteById(id);
        log.info("=== УСПЕШНО: Запись удалена ===");
    }
}