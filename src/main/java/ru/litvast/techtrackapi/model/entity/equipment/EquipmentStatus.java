package ru.litvast.techtrackapi.model.entity.equipment;

public enum EquipmentStatus {
    IN_STOCK,      // на складе
    ASSIGNED,      // выдан сотруднику
    BROKEN,        // сломан
    REPAIR,        // в ремонте
    WRITTEN_OFF    // списан
}