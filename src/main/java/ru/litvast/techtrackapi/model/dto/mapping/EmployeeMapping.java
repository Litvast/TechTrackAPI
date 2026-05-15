package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import ru.litvast.techtrackapi.model.dto.EmployeeDto;
import ru.litvast.techtrackapi.model.dto.EmployeeUpdateDto;
import ru.litvast.techtrackapi.model.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapping {
    EmployeeDto toDto(Employee employee);
    Employee toEntity(EmployeeDto dto);
    Employee toEntity(EmployeeUpdateDto dto);
}