package ru.litvast.techtrackapi.model.entity.equipment.computer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ram")
public class Ram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private String manufacturer;

    @Enumerated(EnumType.STRING)
    private RamType type;

    @Enumerated(EnumType.STRING)
    private RamFormFactor formFactor;

    private Integer capacityMb;
    private Integer frequencyMHz;
    private String timings;
    private Double voltage;

    private Boolean ecc;
    private Boolean registered;
    private Boolean xmpSupport;
    private Boolean expoSupport;
    private Boolean dualRank;
    private Boolean onDieEcc;
}