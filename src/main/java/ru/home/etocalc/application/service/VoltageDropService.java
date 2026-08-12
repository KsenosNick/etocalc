package ru.home.etocalc.application.service;

import org.springframework.stereotype.Service;
import ru.home.etocalc.calculator.voltagedrop.AmbientCondition;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropCalculator;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropInput;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropResult;
import ru.home.etocalc.domain.model.catalog.Cable;

import java.math.BigDecimal;

@Service
public class VoltageDropService {

    private final VoltageDropCalculator calculator = new VoltageDropCalculator();

    public VoltageDropResult calculateForCable(
            Cable cable,
            double networkVoltage,
            double totalCurrentA,
            double lengthM,
            double cosPhi,
            double ambientTempC,
            int parallelCables
    ) {
        BigDecimal alpha = cable.getCableType().getConductorMaterial().getResistivity()
                .compareTo(new BigDecimal("0.0175")) == 0
                ? new BigDecimal("0.00393")  // Медь
                : new BigDecimal("0.00403"); // Алюминий

        VoltageDropInput input = new VoltageDropInput(
                BigDecimal.valueOf(networkVoltage),
                new BigDecimal(cable.getNominalVoltage()),
                BigDecimal.valueOf(totalCurrentA),
                BigDecimal.valueOf(lengthM),
                cable.getConductorCount(),
                parallelCables,
                cable.getRPhActive(),
                cable.getXPhReactive(),
                BigDecimal.valueOf(cosPhi),
                alpha,
                new AmbientCondition(BigDecimal.valueOf(ambientTempC))
        );

        return calculator.calculate(input);
    }
}