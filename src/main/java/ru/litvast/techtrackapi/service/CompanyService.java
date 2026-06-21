package ru.litvast.techtrackapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapping companyMapping;

    @Transactional
    public CompanyDto addCompany(CompanyDto dto) {
        log.info("=== НАЧАЛО: Добавление компании ===");
        log.info("Название: {}, ИНН: {}", dto.getName(), dto.getInn());

        if (dto.getId() != null) {
            log.error("Передан ID при создании компании. ID: {}", dto.getId());
            throw new IllegalArgumentException("To create a company, you must specify a name, not an ID");
        }

        validateAddCompany(dto);

        Company company = companyMapping.toEntity(dto);
        companyRepository.save(company);

        log.info("Компания создана. ID: {}", company.getId());
        log.info("=== УСПЕШНО: Компания добавлена ===");

        return companyMapping.toDto(company);
    }

    public Page<CompanyDto> getAllCompanies(Pageable pageable) {
        log.debug("Запрос всех компаний с пагинацией");

        Page<Company> companies = companyRepository.findAll(pageable);
        if (companies.isEmpty()) {
            log.warn("Компании не найдены");
            throw new NoEntitiesFoundException("No companies found");
        }

        log.debug("Найдено {} компаний", companies.getTotalElements());
        return companies.map(companyMapping::toDto);
    }

    public CompanyDto getCompanyById(Long id) {
        log.debug("Поиск компании по ID: {}", id);

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Компания с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Company with id '%d' not found", id)
                    );
                });

        return companyMapping.toDto(company);
    }

    public CompanyDto getCompanyByName(String name) {
        log.debug("Поиск компании по названию: {}", name);

        Company company = companyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> {
                    log.error("Компания с названием '{}' не найдена", name);
                    return new EntityNotFoundException(
                            String.format("Company with name '%s' not found", name)
                    );
                });

        return companyMapping.toDto(company);
    }

    public CompanyDto getCompanyByInn(String inn) {
        log.debug("Поиск компании по ИНН: {}", inn);

        Company company = companyRepository.findByInn(inn)
                .orElseThrow(() -> {
                    log.error("Компания с ИНН '{}' не найдена", inn);
                    return new EntityNotFoundException(
                            String.format("Company with INN '%s' not found", inn)
                    );
                });

        return companyMapping.toDto(company);
    }

    public CompanyDto getCompanyByRoomId(long roomId) {
        log.debug("Поиск компании по айди кабинета: {}", roomId);

        Company company = companyRepository.findByBuildings_Floors_Rooms_Id(roomId)
                .orElseThrow(() -> {
                    log.error("Компания с кабинетом по айди '{}' не найдена", roomId);
                    return new EntityNotFoundException(
                            String.format("Company with room id '%d' not found", roomId)
                    );
                });

        return companyMapping.toDto(company);
    }

    public long getCountCompanies() {
        log.debug("Подсчёт общего количества компаний");
        return companyRepository.count();
    }

    @Transactional
    public CompanyDto updateCompany(Long id, CompanyUpdateDto dto) {
        log.info("=== НАЧАЛО: Обновление компании ===");
        log.info("ID компании: {}", id);

        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Компания с ID {} не найдена", id);
                    return new EntityNotFoundException(
                            String.format("Company with id '%d' not found", id)
                    );
                });

        if (dto.getName() != null && !existingCompany.getName().equalsIgnoreCase(dto.getName())) {
            log.info("Изменение названия: {} -> {}", existingCompany.getName(), dto.getName());

            if (companyRepository.existsByNameIgnoreCase(dto.getName())) {
                log.warn("Компания с названием '{}' уже существует", dto.getName());
                throw new IllegalArgumentException(
                        String.format("Company '%s' is already taken", dto.getName())
                );
            }
            existingCompany.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            log.info("Обновление описания");
            existingCompany.setDescription(dto.getDescription());
        }

        if (dto.getInn() != null) {
            log.info("Обновление ИНН: {} -> {}", existingCompany.getInn(), dto.getInn());
            existingCompany.setInn(dto.getInn());
        }

        if (dto.getKpp() != null) {
            log.info("Обновление КПП: {} -> {}", existingCompany.getKpp(), dto.getKpp());
            existingCompany.setKpp(dto.getKpp());
        }

        if (dto.getOgrn() != null) {
            log.info("Обновление ОГРН: {} -> {}", existingCompany.getOgrn(), dto.getOgrn());
            existingCompany.setOgrn(dto.getOgrn());
        }

        if (dto.getAddress() != null) {
            log.info("Обновление адреса: {}", dto.getAddress());
            existingCompany.setAddress(dto.getAddress());
        }

        if (dto.getPhone() != null) {
            log.info("Обновление телефона: {}", dto.getPhone());
            existingCompany.setPhone(dto.getPhone());
        }

        if (dto.getEmail() != null) {
            log.info("Обновление email: {}", dto.getEmail());
            existingCompany.setEmail(dto.getEmail());
        }

        companyRepository.save(existingCompany);
        log.info("=== УСПЕШНО: Компания обновлена ===");

        return companyMapping.toDto(existingCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        log.info("=== НАЧАЛО: Удаление компании ===");
        log.info("ID компании: {}", id);

        if (!companyRepository.existsById(id)) {
            log.error("Компания с ID {} не найдена", id);
            throw new EntityNotFoundException(
                    String.format("Company with id '%d' not found", id)
            );
        }

        companyRepository.deleteById(id);
        log.info("=== УСПЕШНО: Компания удалена ===");
    }

    public void validateAddCompany(CompanyDto dto) {
        if (companyRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Компания с названием '{}' уже существует", dto.getName());
            throw new IllegalArgumentException(
                    String.format("Company '%s' is already taken", dto.getName())
            );
        }
        if (dto.getInn() != null && companyRepository.findByInn(dto.getInn()).isPresent()) {
            log.warn("Компания с ИНН '{}' уже существует", dto.getInn());
            throw new IllegalArgumentException(
                    String.format("Company with INN '%s' already exists", dto.getInn())
            );
        }
    }
}