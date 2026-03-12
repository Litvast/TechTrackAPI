package ru.litvast.techtrackapi.model.entity.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RamFormFactor {

    // Десктопные
    DIMM("DIMM", "Dual In-line Memory Module", "Desktop/Server"),
    UDIMM("UDIMM", "Unbuffered DIMM", "Desktop"),

    // Ноутбучные
    SODIMM("SO-DIMM", "Small Outline DIMM", "Laptop"),
    MICRO_DIMM("Micro-DIMM", "Micro Dual In-line Memory Module", "Ultra-thin laptops"),

    // Серверные
    RDIMM("RDIMM", "Registered DIMM", "Server"),
    LRDIMM("LRDIMM", "Load-Reduced DIMM", "High-end server"),
    MINI_RDIMM("Mini-RDIMM", "Mini Registered DIMM", "Blade servers"),
    MINI_LRDIMM("Mini-LRDIMM", "Mini Load-Reduced DIMM", "Blade servers"),

    // Специализированные
    CAMM("CAMM", "Compression Attached Memory Module", "Laptop (Dell proprietary)"),
    CAMM2("CAMM2", "Compression Attached Memory Module 2", "Laptop (new JEDEC standard)"),

    // Встраиваемые
    LPDIMM("LPDIMM", "Low Power DIMM", "Embedded systems"),
    ECC_SODIMM("ECC SO-DIMM", "ECC Small Outline DIMM", "Workstation laptops"),

    // Устаревшие
    SIP("SIP", "Single In-line Package", "Very old computers"),
    SIPP("SIPP", "Single In-line Pin Package", "Old computers"),
    SIMM_30("SIMM 30-pin", "Single In-line Memory Module 30-pin", "Old PCs (386/486)"),
    SIMM_72("SIMM 72-pin", "Single In-line Memory Module 72-pin", "Old PCs (486/Pentium)"),
    RIMM("RIMM", "Rambus In-line Memory Module", "Rambus systems");

    private final String code;
    private final String name;
    private final String usage;
}
