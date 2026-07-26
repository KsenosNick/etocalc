package ru.home.etocalc.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SheathMaterial {
    PVC("ПВХ пластикат"),
    PVC_REDUCED_FIRE_HAZARD("ПВХ пластикат пониженной пожарной опасности"),
    CROSSED_LINKED_POLYETHYLENE("Сшитый полиэтилен"),
    RUBBER("Резина"),
    IMPREGNATED_PAPER("Бумага, пропитанная вязким изоляционным составом");

    private final String displayName;

    SheathMaterial(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}