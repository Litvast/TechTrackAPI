package ru.litvast.techtrackapi.model.entity.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PsuModular {
    FULL("Fully Modular", "All cables detachable"),
    SEMI("Semi Modular", "Main cables fixed, others detachable"),
    NON_MODULAR("Non-Modular", "All cables fixed");

    private final String name;
    private final String description;
}
