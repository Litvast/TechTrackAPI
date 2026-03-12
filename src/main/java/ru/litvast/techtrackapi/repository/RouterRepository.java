package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.Router;

public interface RouterRepository extends JpaRepository<Router, Long> {
}