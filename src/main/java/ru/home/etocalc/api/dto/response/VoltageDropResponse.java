package ru.home.etocalc.api.dto.response;

public record VoltageDropResponse(
        String cableName,              // Полное имя кабеля
        Double deltaUVolts,            // Потеря напряжения, В
        Double deltaUPercent,          // Потеря напряжения, %
        Double endVoltage             // Напряжение в конце линии, В
) {}