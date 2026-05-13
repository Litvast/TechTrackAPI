package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.StorageDevice;

public interface StorageDeviceRepository extends JpaRepository<StorageDevice, Long> {
    Boolean existsByNameIgnoreCase(String name);
}
