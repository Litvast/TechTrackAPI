package ru.litvast.techtrackapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.AssignmentHistory;
import java.util.Optional;

public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, Long> {
    Page<AssignmentHistory> findByEquipmentId(Long equipmentId, Pageable pageable);
    Page<AssignmentHistory> findByEmployeeId(Long employeeId, Pageable pageable);
    Optional<AssignmentHistory> findByEquipmentIdAndReturnedAtIsNull(Long equipmentId);
    long countByReturnedAtIsNull();
}