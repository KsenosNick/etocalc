package ru.home.etocalc.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ConductorMaterial {
    COPPER("Медь"),
    ALUMINUM("Алюминий");

    private final String displayName;

    ConductorMaterial(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}