package ru.home.etocalc.application.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.home.etocalc.api.dto.request.VoltageDropRequest;
import ru.home.etocalc.api.dto.response.VoltageDropResponse;
import ru.home.etocalc.calculator.voltagedrop.AmbientCondition;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropCalculator;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropInput;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropResult;
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

        // Валидация напряжения
        if (request.networkVoltage() > cable.getNominalVoltage()) {
            throw new IllegalArgumentException(
                    String.format("Напряжение сети (%.0f В) превышает номинал кабеля (%d В)",
                            request.networkVoltage(), cable.getNominalVoltage())
            );
        }

        // Определяем температурный коэффициент по материалу жилы
        BigDecimal tempCoefficient = getTemperatureCoefficient(cable.getCableType().getConductorMaterial());

        VoltageDropInput input = new VoltageDropInput(
                BigDecimal.valueOf(request.networkVoltage()),
                BigDecimal.valueOf(cable.getNominalVoltage()),
                BigDecimal.valueOf(request.current()),
                BigDecimal.valueOf(request.length()),
                cable.getConductorCount(),
                request.parallelCableCount(),
                cable.getRPhActive(),
                cable.getXPhReactive(),
                BigDecimal.valueOf(request.cosPhi()),
                tempCoefficient,
                new AmbientCondition(BigDecimal.valueOf(request.ambientTemperature()))
        );

        VoltageDropResult result = calculator.calculate(input);

        return new VoltageDropResponse(
                cable.getFullName(),
                result.deltaUVolts().doubleValue(),
                result.deltaUPercent().doubleValue(),
                result.endVoltage().doubleValue()
        );
    }

    private BigDecimal getTemperatureCoefficient(ConductorMaterial material) {
        // Медь: 0.00393, Алюминий: 0.00403
        return material.getName().toLowerCase().contains("мед")
                ? new BigDecimal("0.00393")
                : new BigDecimal("0.00403");
    }
}