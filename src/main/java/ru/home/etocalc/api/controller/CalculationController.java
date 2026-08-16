package ru.home.etocalc.api.controller;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.home.etocalc.api.dto.request.CalculationSourceType;
import ru.home.etocalc.api.dto.request.VoltageDropRequest;
import ru.home.etocalc.api.dto.response.VoltageDropResponse;
import ru.home.etocalc.application.service.VoltageDropService;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/calculation")
@RequiredArgsConstructor
public class CalculationController {

    private final VoltageDropService voltageDropService;
    private final CableRepository cableRepository;

    @PostMapping("/voltage-drop")
    public VoltageDropResponse calculateVoltageDrop(@Valid @RequestBody VoltageDropRequest request) {
        return voltageDropService.calculate(request);
    }

    @GetMapping("/voltage-drop/demo")
    public VoltageDropResponse demoVoltageDrop(
            @RequestParam(defaultValue = "0") Long cableId,
            @RequestParam(required = false) Double networkVoltage,
            @RequestParam(defaultValue = "16.0") Double current,
            @RequestParam(defaultValue = "50.0") Double length,
            @RequestParam(defaultValue = "0.9") Double cosPhi,
            @RequestParam(defaultValue = "30.0") Double ambientTemperature,
            @RequestParam(defaultValue = "1") Integer parallelCableCount
    ) {
        List<Cable> cables = cableRepository.findAllWithAllMaterials();
        if (cables.isEmpty()) {
            throw new IllegalStateException("Справочник кабелей пуст!");
        }

        Cable cable = cableId > 0
                ? cables.stream().filter(c -> c.getId().equals(cableId)).findFirst().orElse(cables.get(10))
                : cables.get(10);

        // Умное определение напряжения, если не передано
        double voltage = networkVoltage != null
                ? networkVoltage
                : (cable.getConductorCount() <= 3 ? 230.0 : 400.0);

        VoltageDropRequest request = new VoltageDropRequest(
                CalculationSourceType.CABLE,
                cable.getId(),
                voltage,
                current,
                length,
                cosPhi,
                ambientTemperature,
                parallelCableCount
        );

        return voltageDropService.calculate(request);
    }

    @Nonnull
    private static VoltageDropRequest createVoltageDropRequest(Cable cable) {
        double defaultNetworkVoltage = (cable.getConductorCount() <= 3) ? 230.0 : 400.0;

        VoltageDropRequest request = new VoltageDropRequest(
                CalculationSourceType.CABLE,
                cable.getId(),
                defaultNetworkVoltage, // 230В или 400В
                16.0,                  // 16 А
                50.0,                  // 50 м
                0.9,                   // cosφ
                30.0,                  // 30°C
                1                      // 1 кабель
        );
        return request;
    }
}