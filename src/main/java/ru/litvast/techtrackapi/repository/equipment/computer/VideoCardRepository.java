package ru.litvast.techtrackapi.repository.equipment.computer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.computer.VideoCard;

public interface VideoCardRepository extends JpaRepository<VideoCard, Long> {
    Boolean existsByNameIgnoreCase(String name);
}
