package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class CpuSocketService {

    private final CpuSocketRepository cpuSocketRepository;
    private final CpuSocketMapping cpuSocketMapping;

    // CREATE
    @Transactional
    public CpuSocketDto addCpuSocket(CpuSocketDto socketDto) {
        if (socketDto.getId() != null) {
            throw new IllegalArgumentException("To create a socket, you must specify a name, not an ID");
        }

        validateAddCpuSocket(socketDto);

        CpuSocket socket = cpuSocketMapping.toEntity(socketDto);
        cpuSocketRepository.save(socket);
        return cpuSocketMapping.toDto(socket);
    }

    // READ all with pagination
    public Page<CpuSocketDto> getAllCpuSockets(Pageable pageable) {
        Page<CpuSocket> sockets = cpuSocketRepository.findAll(pageable);
        if (sockets.isEmpty()) {
            throw new NoEntitiesFoundException("No CPU sockets found");
        }
        return sockets.map(cpuSocketMapping::toDto);
    }

    // READ by id
    public CpuSocketDto getCpuSocketById(Long id) {
        CpuSocket socket = cpuSocketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CpuSocket with id '%d' not found", id)
                ));
        return cpuSocketMapping.toDto(socket);
    }

    // READ by name
    public CpuSocketDto getCpuSocketByName(String name) {
        CpuSocket socket = cpuSocketRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CpuSocket with name '%s' not found", name)
                ));
        return cpuSocketMapping.toDto(socket);
    }

    // UPDATE
    @Transactional
    public CpuSocketDto updateCpuSocket(Long id, CpuSocketDto socketDto) {
        CpuSocket existingSocket = cpuSocketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CpuSocket with id '%d' not found", id)
                ));

        // Проверка уникальности имени (если изменилось)
        if (!existingSocket.getName().equalsIgnoreCase(socketDto.getName())) {
            if (cpuSocketRepository.existsByNameIgnoreCase(socketDto.getName())) {
                throw new IllegalArgumentException(
                        String.format("CpuSocket '%s' is already taken", socketDto.getName())
                );
            }
        }

        existingSocket.setName(socketDto.getName());
        existingSocket.setManufacturer(socketDto.getManufacturer());
        existingSocket.setCompatibleCpus(socketDto.getCompatibleCpus());

        cpuSocketRepository.save(existingSocket);
        return cpuSocketMapping.toDto(existingSocket);
    }

    // DELETE
    @Transactional
    public void deleteCpuSocket(Long id) {
        if (!cpuSocketRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("CpuSocket with id '%d' not found", id)
            );
        }
        cpuSocketRepository.deleteById(id);
    }

    // Validation
    public void validateAddCpuSocket(CpuSocketDto socketDto) {
        if (cpuSocketRepository.existsByNameIgnoreCase(socketDto.getName())) {
            throw new IllegalArgumentException(
                    String.format("CpuSocket '%s' is already taken", socketDto.getName())
            );
        }
    }
}