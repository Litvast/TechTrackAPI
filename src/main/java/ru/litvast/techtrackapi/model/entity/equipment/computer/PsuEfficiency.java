package ru.litvast.techtrackapi.model.entity.equipment.computer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PsuEfficiency {
    STANDARD("Standard", "No certification"),
    BRONZE("80 Plus Bronze", "82-85% efficiency"),
    SILVER("80 Plus Silver", "85-88% efficiency"),
    GOLD("80 Plus Gold", "87-90% efficiency"),
    PLATINUM("80 Plus Platinum", "89-92% efficiency"),
    TITANIUM("80 Plus Titanium", "90-94% efficiency");

    private final String name;
    private final String description;
}
