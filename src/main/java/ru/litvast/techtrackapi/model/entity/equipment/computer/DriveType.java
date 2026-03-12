package ru.litvast.techtrackapi.model.entity.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DriveType {
    HDD("HDD", "Hard Disk Drive"),
    SSD("SSD", "Solid State Drive"),
    HYBRID("HYBRID", "Hybrid Drive");

    private final String shortName;
    private final String fullName;
}
