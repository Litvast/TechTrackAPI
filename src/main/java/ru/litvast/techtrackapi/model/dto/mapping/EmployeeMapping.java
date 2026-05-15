package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.litvast.techtrackapi.model.dto.EmployeeDto;
import ru.litvast.techtrackapi.model.dto.EmployeeUpdateDto;
import ru.litvast.techtrackapi.model.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapping {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "assignedComputer.id", target = "assignedComputerId")
    @Mapping(source = "assignedPrinter.id", target = "assignedPrinterId")
    EmployeeDto toDto(Employee employee);

    Employee toEntity(EmployeeDto dto);
    Employee toEntity(EmployeeUpdateDto dto);
}