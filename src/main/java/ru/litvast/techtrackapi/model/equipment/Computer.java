package ru.litvast.techtrackapi.model.equipment;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("COMPUTER")
@Table(name = "computers")
public class Computer extends Equipment {

    @Column(name = "processor_id")
    private String processor;

    @Column(name = "motherboard_id")
    private String motherboard;

    @Column(name = "ram_id")
    private String ram;

    @Column(name = "video_card_id")
    private String videoCard;

    @Column(name = "storage_device_id")
    private String storageDevice;

    @Column(name = "power_supply_id")
    private String powerSupply;
}