package ru.home.etocalc.api.dto.response;

import java.util.List;

public record VoltageDropResponse(
        String cableName,
        Double deltaUVolts,
        Double deltaUPercent,
        Double endVoltage,
        List<CalculationStep> steps
) {}