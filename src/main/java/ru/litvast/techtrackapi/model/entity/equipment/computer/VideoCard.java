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
@Table(name = "video_cards")
public class VideoCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    private String manufacturer;

    private String architecture;
    private Integer clockFrequencyMHz;
    private Integer turboClockFrequencyMHz;
    private Integer lithographyNm;
    private Integer numberOfAlus;
    private Integer numberOfTmus;
    private Integer numberOfRops;

    @Enumerated(EnumType.STRING)
    private GpuMemoryType vramType;
    private Integer vramCapacityMb;
    private Integer vramFrequencyMHz;
    private Integer vramBusBit;

    private Integer tdpWatts;

    private String pcieVersion;
}