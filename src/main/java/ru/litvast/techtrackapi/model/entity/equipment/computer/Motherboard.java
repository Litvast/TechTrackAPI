package ru.litvast.techtrackapi.model.entity.equipment.computer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "motherboards")
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private String manufacturer;

    private String chipset;

    @ElementCollection
    @CollectionTable(name = "motherboard_memory_supports")
    private List<MemorySupport> memorySupports;

    @ElementCollection
    @CollectionTable(name = "motherboard_storage_ports")
    private List<StoragePort> storagePorts;

    @ElementCollection
    @CollectionTable(name = "motherboard_io_ports")
    private List<IoPort> ioPorts;

    @ManyToOne
    @JoinColumn(name = "form_factor_id")
    private MotherboardFormFactor formFactor;

    @ManyToOne
    @JoinColumn(name = "socket_id")
    private CpuSocket socket;
}