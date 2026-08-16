package ru.home.etocalc.api.dto.request;

public record VoltageDropRequest(
        CalculationSourceType sourceType,  // Тип источника
        Long sourceId,                     // ID объекта (cableId, busbarId, trolleyId)
        Double networkVoltage,
        Double current,
        Double length,
        Double cosPhi,
        Double ambientTemperature,
        Integer parallelCableCount
) {
    public VoltageDropRequest {
        if (sourceType == null || sourceId == null) {
            throw new IllegalArgumentException("sourceType и sourceId обязательны");
        }
    }
}