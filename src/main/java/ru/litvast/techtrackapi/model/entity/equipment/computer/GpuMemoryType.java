package ru.litvast.techtrackapi.model.entity.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GpuMemoryType {

    // GDDR (Graphics DDR)
    GDDR("GDDR", "GDDR", "Graphics DDR"),
    GDDR2("GDDR2", "GDDR2", "Graphics DDR2"),
    GDDR3("GDDR3", "GDDR3", "Graphics DDR3"),
    GDDR4("GDDR4", "GDDR4", "Graphics DDR4"),
    GDDR5("GDDR5", "GDDR5", "Graphics DDR5"),
    GDDR5X("GDDR5X", "GDDR5X", "Graphics DDR5 eXtended"),
    GDDR6("GDDR6", "GDDR6", "Graphics DDR6"),
    GDDR6X("GDDR6X", "GDDR6X", "Graphics DDR6 eXtended"),
    GDDR6W("GDDR6W", "GDDR6W", "Graphics DDR6 Wide"),
    GDDR7("GDDR7", "GDDR7", "Graphics DDR7"),

    // HBM (High Bandwidth Memory)
    HBM("HBM", "HBM", "High Bandwidth Memory"),
    HBM2("HBM2", "HBM2", "High Bandwidth Memory 2"),
    HBM2E("HBM2E", "HBM2E", "High Bandwidth Memory 2 Enhanced"),
    HBM3("HBM3", "HBM3", "High Bandwidth Memory 3"),
    HBM3E("HBM3E", "HBM3E", "High Bandwidth Memory 3 Enhanced"),

    // ДРУГИЕ
    DDR3_VRAM("DDR3 VRAM", "DDR3", "DDR3 Video Memory"),
    DDR4_VRAM("DDR4 VRAM", "DDR4", "DDR4 Video Memory"),
    SGRAM("SGRAM", "SGRAM", "Synchronous Graphics RAM"),
    WRAM("WRAM", "WRAM", "Window RAM"),
    MDRAM("MDRAM", "MDRAM", "Multibank DRAM");

    private final String code;
    private final String shortName;
    private final String fullName;
}
