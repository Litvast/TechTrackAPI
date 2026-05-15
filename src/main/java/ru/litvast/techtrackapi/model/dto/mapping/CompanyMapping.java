package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.CompanyDto;
import ru.litvast.techtrackapi.model.dto.CompanyUpdateDto;
import ru.litvast.techtrackapi.model.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapping {
    CompanyDto toDto(Company company);
    Company toEntity(CompanyDto dto);
    Company toEntity(CompanyUpdateDto dto);
}