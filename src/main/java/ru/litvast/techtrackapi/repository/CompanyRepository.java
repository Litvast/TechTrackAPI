package ru.litvast.techtrackapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvast.techtrackapi.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}