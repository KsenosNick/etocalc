package ru.home.etocalc.calculator.voltagedrop;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Результат расчёта потерь напряжения.
 */
public record VoltageDropResult(
        BigDecimal deltaUVolts,      // Потеря напряжения в Вольтах
        BigDecimal deltaUPercent,    // Потеря напряжения в %
        BigDecimal endVoltage        // Напряжение в конце линии
) {
    public VoltageDropResult roundTo(int scale) {
        return new VoltageDropResult(
                deltaUVolts.setScale(scale, RoundingMode.HALF_UP),
                deltaUPercent.setScale(scale, RoundingMode.HALF_UP),
                endVoltage.setScale(scale, RoundingMode.HALF_UP)
        );
    }
}