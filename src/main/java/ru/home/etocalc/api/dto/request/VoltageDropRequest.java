package ru.home.etocalc.api.dto.request;

import jakarta.validation.constraints.*;

public record VoltageDropRequest(
        @NotNull CalculationSourceType sourceType,
        @NotNull Long sourceId,

        @NotNull @DecimalMin("10") Double networkVoltage,
        @NotNull @DecimalMin("10") Double targetVoltage,

        @NotNull @DecimalMin("0.1") Double current, // Всегда используется для расчёта ΔU
        @DecimalMin("0") Double power,              // НОВОЕ: Опциональная мощность (кВт)

        @NotNull @DecimalMin("1") Double length,
        @NotNull @DecimalMin("0.01") @DecimalMax("1") Double cosPhi,
        @NotNull @DecimalMin("-40") @DecimalMax("60") Double ambientTemperature,
        @NotNull @Min(1) Integer parallelCableCount
) {}