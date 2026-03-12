package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.equipment.Printer;

public interface PrinterRepository extends JpaRepository<Printer, Long> {
}