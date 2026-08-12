package ru.home.etocalc.calculator.voltagedrop;

import java.math.BigDecimal;

/**
 * Условия окружающей среды для расчёта.
 * Влияют на активное сопротивление проводника.
 */
public record AmbientCondition(
        BigDecimal temperature  // Температура окружающей среды, °C
) {
    public AmbientCondition {
        if (temperature == null) {
            throw new IllegalArgumentException("Температура не может быть null");
        }
    }

    /**
     * Создаёт условия с температурой по умолчанию (20°C - стандартные условия).
     */
    public static AmbientCondition standard() {
        return new AmbientCondition(new BigDecimal("20"));
    }
}