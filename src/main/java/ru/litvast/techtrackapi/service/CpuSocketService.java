package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.equipment.computer.CpuSocketDto;
import ru.litvast.techtrackapi.model.dto.mapping.equipment.computer.CpuSocketMapping;
import ru.litvast.techtrackapi.model.entity.equipment.computer.CpuSocket;
import ru.litvast.techtrackapi.repository.equipment.computer.CpuSocketRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CpuSocketService {

    private final CpuSocketRepository cpuSocketRepository;
    private final CpuSocketMapping cpuSocketMapping;

    // CREATE
    @Transactional
    public CpuSocketDto addCpuSocket(CpuSocketDto socketDto) {
        log.info("=== НАЧАЛО: Добавление сокета процессора ===");
        log.info("Название: {}", socketDto.getName());

        if (socketDto.getId() != null) {
            log.error("Передан ID при создании сокета. ID: {}", socketDto.getId());
            throw new IllegalArgumentException("To create a socket, you must specify a name, not an ID");
        }

        validateAddCpuSocket(socketDto);

        CpuSocket socket = cpuSocketMapping.toEntity(socketDto);
        cpuSocketRepository.save(socket);

        log.info("Сокет создан. ID: {}", socket.getId());
        log.info("=== УСПЕШНО: Сокет добавлен ===");

        return cpuSocketMapping.toDto(socket);
    }

    // READ all with pagination
    public Page<CpuSocketDto> getAllCpuSockets(Pageable pageable) {
        log.debug("Запрос всех сокетов процессоров с пагинацией");

        Page<CpuSocket> sockets = cpuSocketRepository.findAll(pageable);
        if (sockets.isEmpty()) {
            log.warn("Сокеты не найдены");
            throw new NoEntitiesFoundException("No CPU sockets found");
        }

        log.debug("Найдено {} сокетов", sockets.getTotalElements());
        return sockets.map(cpuSocketMapping::toDto);
    }

    // READ by id
    public CpuSocketDto getCpuSocketById(Long id) {
        log.debug("Поиск сокета по ID: {}", id);

        CpuSocket socket = cpuSocketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Сокет с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("CpuSocket with id '%d' not found", id)
                    );
                });

        return cpuSocketMapping.toDto(socket);
    }

    // READ by name
    public CpuSocketDto getCpuSocketByName(String name) {
        log.debug("Поиск сокета по названию: {}", name);

        CpuSocket socket = cpuSocketRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Сокет с названием '{}' не найден", name);
                    return new EntityNotFoundException(
                            String.format("CpuSocket with name '%s' not found", name)
                    );
                });

        return cpuSocketMapping.toDto(socket);
    }

    // UPDATE
    @Transactional
    public CpuSocketDto updateCpuSocket(Long id, CpuSocketDto socketDto) {
        log.info("=== НАЧАЛО: Обновление сокета процессора ===");
        log.info("ID сокета: {}", id);

        CpuSocket existingSocket = cpuSocketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Сокет с ID {} не найден", id);
                    return new EntityNotFoundException(
                            String.format("CpuSocket with id '%d' not found", id)
                    );
                });

        if (!existingSocket.getName().equalsIgnoreCase(socketDto.getName())) {
            log.info("Изменение названия: {} -> {}", existingSocket.getName(), socketDto.getName());

            if (cpuSocketRepository.existsByNameIgnoreCase(socketDto.getName())) {
                log.warn("Сокет с названием '{}' уже существует", socketDto.getName());
                throw new IllegalArgumentException(
                        String.format("CpuSocket '%s' is already taken", socketDto.getName())
                );
            }
            existingSocket.setName(socketDto.getName());
        }

        if (socketDto.getManufacturer() != null) {
            log.info("Изменение производителя: {} -> {}", existingSocket.getManufacturer(), socketDto.getManufacturer());
            existingSocket.setManufacturer(socketDto.getManufacturer());
        }

        if (socketDto.getCompatibleCpus() != null) {
            log.info("Обновление списка совместимых процессоров");
            existingSocket.setCompatibleCpus(socketDto.getCompatibleCpus());
        }

        cpuSocketRepository.save(existingSocket);
        log.info("=== УСПЕШНО: Сокет обновлён ===");

        return cpuSocketMapping.toDto(existingSocket);
    }

    // DELETE
    @Transactional
    public void deleteCpuSocket(Long id) {
        log.info("=== НАЧАЛО: Удаление сокета процессора ===");
        log.info("ID сокета: {}", id);

        if (!cpuSocketRepository.existsById(id)) {
            log.error("Сокет с ID {} не найден", id);
            throw new EntityNotFoundException(
                    String.format("CpuSocket with id '%d' not found", id)
            );
        }

        cpuSocketRepository.deleteById(id);
        log.info("=== УСПЕШНО: Сокет удалён ===");
    }

    // Validation
    public void validateAddCpuSocket(CpuSocketDto socketDto) {
        if (cpuSocketRepository.existsByNameIgnoreCase(socketDto.getName())) {
            log.warn("Сокет с названием '{}' уже существует", socketDto.getName());
            throw new IllegalArgumentException(
                    String.format("CpuSocket '%s' is already taken", socketDto.getName())
            );
        }
    }
}