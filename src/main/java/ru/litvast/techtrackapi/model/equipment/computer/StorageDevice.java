package ru.litvast.techtrackapi.model.equipment.computer;

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
@Table(name = "storage_devices")
public class StorageDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    private String manufacture;

    private Integer capacityGb;

    private String connectionInterface;
    private String formFactor;

    private Integer readSpeedMbps;
    private Integer writeSpeedMbps;

    // SSD
    private String nandType;
    private Integer tbw;

    // HDD
    private Integer rpm;
    private Double heightMm;
}