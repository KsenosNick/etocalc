package ru.home.etocalc.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.home.etocalc.api.dto.request.VoltageDropRequest;
import ru.home.etocalc.api.dto.response.VoltageDropResponse;
import ru.home.etocalc.calculator.voltagedrop.AmbientCondition;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropCalculator;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropInput;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropResult;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropTrace;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.domain.model.catalog.ConductorMaterial;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class VoltageDropService {

    private final CableRepository cableRepository;
    private final VoltageDropCalculator calculator;

    public VoltageDropResponse calculate(VoltageDropRequest request) {
        return switch (request.sourceType()) {
            case CABLE -> calculateForCable(request);
            case BUSBAR -> throw new UnsupportedOperationException("Расчёт для шинопроводов пока не реализован");
            case TROLLEY -> throw new UnsupportedOperationException("Расчёт для троллеев пока не реализован");
        };
    }

    private VoltageDropResponse calculateForCable(VoltageDropRequest request) {
        Cable cable = cableRepository.findByIdWithAllMaterials(request.sourceId())
                .orElseThrow(() -> new IllegalArgumentException("Кабель с ID " + request.sourceId() + " не найден"));

        if (request.networkVoltage() > cable.getNominalVoltage()) {
            throw new IllegalArgumentException(
                    String.format("Напряжение сети (%.0f В) превышает номинал кабеля (%d В)",
                            request.networkVoltage(), cable.getNominalVoltage())
            );
        }

        BigDecimal tempCoefficient = getTemperatureCoefficient(cable.getCableType().getConductorMaterial());
        
        boolean isThreePhase = request.networkVoltage() >= 380.0;
        double sqrt3 = Math.sqrt(3.0);
        
        double calculatedCurrent = request.current();
        if (request.power() != null && request.power() > 0) {
            double voltageKv = request.networkVoltage() / 1000.0;
            if (isThreePhase) {
                // I = P / (√3 * U * cosφ)
                calculatedCurrent = request.power() / (sqrt3 * voltageKv * request.cosPhi());
            } else {
                // I = P / (U * cosφ)
                calculatedCurrent = request.power() / (voltageKv * request.cosPhi());
            }
        }

        VoltageDropInput input = new VoltageDropInput(
                BigDecimal.valueOf(request.networkVoltage()),
                BigDecimal.valueOf(cable.getNominalVoltage()),
                BigDecimal.valueOf(calculatedCurrent), // <-- Используем пересчитанный ток
                BigDecimal.valueOf(request.length()),
                cable.getConductorCount(),
                request.parallelCableCount(),
                cable.getRPhActive(),
                cable.getXPhReactive(),
                BigDecimal.valueOf(request.cosPhi()),
                tempCoefficient,
                new AmbientCondition(BigDecimal.valueOf(request.ambientTemperature()))
        );

        VoltageDropTrace traceResult = calculator.calculateWithTrace(input);
        VoltageDropResult result = traceResult.result();

        return new VoltageDropResponse(
                cable.getFullName(),
                result.deltaUVolts().doubleValue(),
                result.deltaUPercent().doubleValue(),
                result.endVoltage().doubleValue(),
                request.targetVoltage(),
                traceResult.trace().getSteps()
        );
    }

    private BigDecimal getTemperatureCoefficient(ConductorMaterial material) {
        // Медь: 0.00393, Алюминий: 0.00403
        return material.getName().toLowerCase().contains("мед")
                ? new BigDecimal("0.00393")
                : new BigDecimal("0.00403");
    }
}