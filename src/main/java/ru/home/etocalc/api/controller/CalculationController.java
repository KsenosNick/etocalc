package ru.home.etocalc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.home.etocalc.application.service.VoltageDropService;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropResult;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import java.util.List;

@RestController
@RequestMapping("/api/calculation")
@RequiredArgsConstructor
public class CalculationController {

    private final VoltageDropService voltageDropService;
    private final CableRepository cableRepository;

    @GetMapping("/voltage-drop/demo")
    public VoltageDropResult demoVoltageDrop() {
        List<Cable> cables = cableRepository.findAllWithAllMaterials();
        if (cables.isEmpty()) {
            throw new IllegalStateException("Справочник кабелей пуст!");
        }

        Cable cable = cables.get(10); // Например, ВВГнг(А)-LS 3x2.5 (660В)

        // Умное определение напряжения сети по умолчанию
        double defaultNetworkVoltage = (cable.getConductorCount() <= 3) ? 230.0 : 400.0;

        return voltageDropService.calculateForCable(
                cable,
                defaultNetworkVoltage, // 230В или 400В (что меньше номинала кабеля 660В, валидация пройдёт)
                45.0,                  // 16 А
                500.0,                  // 50 м
                0.9,                   // cosφ
                30.0,                  // 30°C
                1                      // 1 кабель
        );
    }
}