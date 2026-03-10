package ru.litvast.techtrackapi.model.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RamType {

    // Для компьютеров
    DDR("DDR", "Desktop"),
    DDR2("DDR2", "Desktop"),
    DDR3("DDR3", "Desktop"),
    DDR4("DDR4", "Desktop"),
    DDR5("DDR5", "Desktop"),
    DDR3L("DDR3L", "Desktop"),
    DDR3U("DDR3U", "Desktop"),
    DDR4L("DDR4L", "Desktop"),

    // Для ноутбуков
    LPDDR("LPDDR", "Mobile"),
    LPDDR2("LPDDR2", "Mobile"),
    LPDDR3("LPDDR3", "Mobile"),
    LPDDR4("LPDDR4", "Mobile"),
    LPDDR4X("LPDDR4X", "Mobile"),
    LPDDR5("LPDDR5", "Mobile"),
    LPDDR5X("LPDDR5X", "Mobile"),
    LPDDR5T("LPDDR5T", "Mobile"),
    LPDDR6("LPDDR6", "Mobile"),

    // Устаревшие
    SDRAM("SDRAM", "Legacy"),
    RDRAM("RDRAM", "Legacy");

    private final String name;
    private final String category;
}
