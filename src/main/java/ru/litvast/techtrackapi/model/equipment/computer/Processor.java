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
@Table(name = "processors")
public class Processor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private String manufacturer;

    private Double clockFrequencyGHz;
    private Double turboClockFrequencyGHz;

    private Integer numberOfCores;
    private Integer numberOfThreads;

    private Integer l1CacheKB;
    private Integer l2CacheKB;
    private Integer l3CacheMB;

    private Integer tdpWatts;

    private Integer lithographyNm;

    @ManyToOne
    @JoinColumn(name = "cpu_architecture_id")
    private CpuArchitecture architecture;

    @ManyToOne
    @JoinColumn(name = "socket_id")
    private CpuSocket socket;
}
