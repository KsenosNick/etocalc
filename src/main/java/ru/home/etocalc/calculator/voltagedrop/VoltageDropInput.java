package ru.home.etocalc.calculator.voltagedrop;

import java.math.BigDecimal;

public record VoltageDropInput(
        BigDecimal networkVoltage,         // Реальное напряжение сети в начале линии, В
        BigDecimal cableNominalVoltage,    // Номинальное напряжение кабеля, В (для валидации)
        BigDecimal current,                // Общий расчётный ток, А
        BigDecimal length,                 // Длина линии, м
        Integer conductorCount,            // Количество проводников (2-5)
        Integer parallelCableCount,        // Количество параллельных кабелей (>= 1)
        BigDecimal rPhActive,              // Активное сопротивление фазной жилы при 20°C, мОм/м
        BigDecimal xPhReactive,            // Индуктивное сопротивление фазной жилы, Ом/км
        BigDecimal cosPhi,                 // Коэффициент мощности
        BigDecimal temperatureCoefficient, // Температурный коэффициент α, 1/°C
        AmbientCondition ambientCondition  // Условия окружающей среды
) {
    public VoltageDropInput {
        if (networkVoltage == null || cableNominalVoltage == null || current == null || length == null ||
                conductorCount == null || parallelCableCount == null || rPhActive == null || xPhReactive == null ||
                cosPhi == null || temperatureCoefficient == null || ambientCondition == null) {
            throw new IllegalArgumentException("Все параметры должны быть заполнены");
        }

        if (networkVoltage.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Напряжение сети должно быть больше 0");
        }

        if (networkVoltage.compareTo(cableNominalVoltage) > 0) {
            throw new IllegalArgumentException(
                    String.format("Напряжение сети (%s В) не может превышать номинальное напряжение кабеля (%s В)",
                            networkVoltage, cableNominalVoltage)
            );
        }

        if (conductorCount < 2 || conductorCount > 5) {
            throw new IllegalArgumentException("Количество проводников должно быть от 2 до 5");
        }

        if (parallelCableCount < 1) {
            throw new IllegalArgumentException("Количество параллельных кабелей должно быть >= 1");
        }

        if (cosPhi.compareTo(BigDecimal.ZERO) <= 0 || cosPhi.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("cosφ должен быть в диапазоне [0, 1]");
        }
    }

    public BigDecimal getSystemFactor() {
        return (conductorCount == 2 || conductorCount == 3)
                ? new BigDecimal("2")
                : new BigDecimal("1.73205081"); // √3
    }
}