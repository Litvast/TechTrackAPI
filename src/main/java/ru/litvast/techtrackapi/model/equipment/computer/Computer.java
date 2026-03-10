package ru.litvast.techtrackapi.model.equipment.computer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.litvast.techtrackapi.model.equipment.Equipment;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("COMPUTER")
@Table(name = "computers")
public class Computer extends Equipment {

    @ManyToOne
    @JoinColumn(name = "processor_id")
    private Processor processor;

    @ManyToOne
    @JoinColumn(name = "motherboard_id")
    private Motherboard motherboard;

    @ElementCollection
    @CollectionTable(name = "computer_ram")
    private List<Ram> rams;

    @ManyToOne
    @JoinColumn(name = "video_card_id")
    private VideoCard videoCard;

    @ElementCollection
    @CollectionTable(name = "computer_storage_device")
    private List<StorageDevice> storageDevices;

    @ManyToOne
    @JoinColumn(name = "power_supply_id")
    private PowerSupply powerSupply;
}