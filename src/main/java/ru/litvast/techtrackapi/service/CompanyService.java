package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.litvast.techtrackapi.exception.EntityNotFoundException;
import ru.litvast.techtrackapi.exception.NoEntitiesFoundException;
import ru.litvast.techtrackapi.model.dto.CompanyDto;
import ru.litvast.techtrackapi.model.dto.CompanyUpdateDto;
import ru.litvast.techtrackapi.model.dto.mapping.CompanyMapping;
import ru.litvast.techtrackapi.model.entity.Company;
import ru.litvast.techtrackapi.repository.CompanyRepository;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapping companyMapping;

    @Transactional
    public CompanyDto addCompany(CompanyDto dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("To create a company, you must specify a name, not an ID");
        }

        validateAddCompany(dto);

        Company company = companyMapping.toEntity(dto);
        companyRepository.save(company);
        return companyMapping.toDto(company);
    }

    public Page<CompanyDto> getAllCompanies(Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(pageable);
        if (companies.isEmpty()) {
            throw new NoEntitiesFoundException("No companies found");
        }
        return companies.map(companyMapping::toDto);
    }

    public CompanyDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Company with id '%d' not found", id)
                ));
        return companyMapping.toDto(company);
    }

    public CompanyDto getCompanyByName(String name) {
        Company company = companyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Company with name '%s' not found", name)
                ));
        return companyMapping.toDto(company);
    }

    public CompanyDto getCompanyByInn(String inn) {
        Company company = companyRepository.findByInn(inn)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Company with INN '%s' not found", inn)
                ));
        return companyMapping.toDto(company);
    }

    @Transactional
    public CompanyDto updateCompany(Long id, CompanyUpdateDto dto) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Company with id '%d' not found", id)
                ));

        if (dto.getName() != null && !existingCompany.getName().equalsIgnoreCase(dto.getName())) {
            if (companyRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new IllegalArgumentException(
                        String.format("Company '%s' is already taken", dto.getName())
                );
            }
            existingCompany.setName(dto.getName());
        }

        if (dto.getDescription() != null) existingCompany.setDescription(dto.getDescription());
        if (dto.getInn() != null) existingCompany.setInn(dto.getInn());
        if (dto.getKpp() != null) existingCompany.setKpp(dto.getKpp());
        if (dto.getOgrn() != null) existingCompany.setOgrn(dto.getOgrn());
        if (dto.getAddress() != null) existingCompany.setAddress(dto.getAddress());
        if (dto.getPhone() != null) existingCompany.setPhone(dto.getPhone());
        if (dto.getEmail() != null) existingCompany.setEmail(dto.getEmail());

        companyRepository.save(existingCompany);
        return companyMapping.toDto(existingCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    String.format("Company with id '%d' not found", id)
            );
        }
        companyRepository.deleteById(id);
    }

    public void validateAddCompany(CompanyDto dto) {
        if (companyRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException(
                    String.format("Company '%s' is already taken", dto.getName())
            );
        }
        if (dto.getInn() != null && companyRepository.findByInn(dto.getInn()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Company with INN '%s' already exists", dto.getInn())
            );
        }
    }
}