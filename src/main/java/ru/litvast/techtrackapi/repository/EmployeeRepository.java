package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByFullNameIgnoreCase(String fullName);
    Optional<Employee> findByFullNameIgnoreCase(String fullName);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByUser_UsernameIgnoreCase(String username);
    long countByRoom_BuildingFloor_Building_Company_Id(long id);
}