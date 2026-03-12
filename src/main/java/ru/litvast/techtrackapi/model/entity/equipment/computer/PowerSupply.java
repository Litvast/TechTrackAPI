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
@Table(name = "power_supplies")
public class PowerSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    private String description;

    private Integer powerWatts;

    @Enumerated(EnumType.STRING)
    private PsuEfficiency efficiency;

    @Enumerated(EnumType.STRING)
    private PsuFormFactor formFactor;

    @Enumerated(EnumType.STRING)
    private PsuModular modular;
}