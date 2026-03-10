package ru.litvast.techtrackapi.model.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PsuFormFactor {
    ATX("ATX", "Advanced Technology eXtended"),
    MICRO_ATX("Micro-ATX", "Micro Advanced Technology eXtended"),
    SFX("SFX", "Small Form Factor"),
    SFX_L("SFX-L", "Small Form Factor Long"),
    TFX("TFX", "Thin Form Factor"),
    FLEX_ATX("Flex ATX", "Flex Advanced Technology eXtended"),
    EPS("EPS", "Entry-Level Power Supply"),
    MINI_ITX("Mini-ITX", "Mini Information Technology eXtended"),
    CFX("CFX", "Compact Form Factor"),
    LFX("LFX", "Low Profile Form Factor");

    private final String shortName;
    private final String fullName;
}
