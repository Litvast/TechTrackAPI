package ru.litvast.techtrackapi.model.dto.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.litvast.techtrackapi.model.dto.AssignmentHistoryDto;
import ru.litvast.techtrackapi.model.dto.CreateAssignmentDto;
import ru.litvast.techtrackapi.model.dto.ReturnEquipmentDto;
import ru.litvast.techtrackapi.model.entity.AssignmentHistory;

@Mapper(componentModel = "spring")
public interface AssignmentHistoryMapping {

    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "equipment.name", target = "equipmentName")
    @Mapping(source = "equipment.type", target = "equipmentType")
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.fullName", target = "employeeName")
    AssignmentHistoryDto toDto(AssignmentHistory history);

    AssignmentHistory toEntity(CreateAssignmentDto dto);
    AssignmentHistory toEntity(ReturnEquipmentDto dto);
}