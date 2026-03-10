package ru.litvast.techtrackapi.model.equipment.computer;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MemorySupport {

    @Enumerated(EnumType.STRING)
    private RamType type;

    @Enumerated(EnumType.STRING)
    private RamFormFactor formFactor;

    private Integer numberOfSlots;

    private Integer maxMemoryGb;
    private Integer maxFrequencyMHz;
    private Integer maxMemoryChannels;

    private Boolean eccSupported;
    private Boolean nonEccSupported;
}
