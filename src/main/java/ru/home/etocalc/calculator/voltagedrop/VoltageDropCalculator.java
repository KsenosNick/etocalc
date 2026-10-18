package ru.home.etocalc.calculator.voltagedrop;

import org.springframework.stereotype.Component;
import ru.home.etocalc.calculator.CalculationTrace;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Component
public class VoltageDropCalculator {

    private static final BigDecimal TWENTY = new BigDecimal("20");
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private static final DecimalFormat FORMAT = new DecimalFormat("#.######");

    public VoltageDropResult calculate(VoltageDropInput input) {
        return calculateWithTrace(input).result();
    }

    public VoltageDropTrace calculateWithTrace(VoltageDropInput input) {
        CalculationTrace trace = new CalculationTrace();

        // 1. Корректировка сопротивления по температуре
        BigDecimal tempDelta = input.ambientCondition().temperature().subtract(TWENTY);
        trace.addStep("temp_delta", "Разница температур",
                "Δt = t − 20",
                fmt(input.ambientCondition().temperature()) + " − 20",
                fmt(tempDelta), "°C");

        BigDecimal tempFactor = BigDecimal.ONE.add(input.temperatureCoefficient().multiply(tempDelta));
        trace.addStep("temp_factor", "Температурный коэффициент",
                "kT = 1 + α·Δt",
                "1 + " + fmt(input.temperatureCoefficient()) + "·" + fmt(tempDelta),
                fmt(tempFactor), "");

        BigDecimal rPhActiveAdjusted = input.rPhActive().multiply(tempFactor);
        trace.addStep("r_adjusted", "Активное сопротивление при рабочей температуре",
                "rT = r20·kT",
                fmt(input.rPhActive()) + "·" + fmt(tempFactor),
                fmt(rPhActiveAdjusted), "мОм/м");

        // 2. Учёт параллельных кабелей
        BigDecimal parallelCount = new BigDecimal(input.parallelCableCount());
        BigDecimal rEffective = rPhActiveAdjusted.divide(parallelCount, MathContext.DECIMAL64);
        trace.addStep("r_effective", "Эффективное активное сопротивление",
                "rэф = rT / n",
                fmt(rPhActiveAdjusted) + " / " + fmt(parallelCount),
                fmt(rEffective), "мОм/м");

        BigDecimal xEffective = input.xPhReactive().divide(parallelCount, MathContext.DECIMAL64);
        trace.addStep("x_effective", "Эффективное реактивное сопротивление",
                "xэф = x / n",
                fmt(input.xPhReactive()) + " / " + fmt(parallelCount),
                fmt(xEffective), "Ом/км");

        // 3. Единицы измерения
        BigDecimal lengthKm = input.length().divide(THOUSAND, MathContext.DECIMAL64);
        trace.addStep("length_km", "Длина в километрах",
                "Lкм = L / 1000",
                fmt(input.length()) + " / 1000",
                fmt(lengthKm), "км");

        // 4. sin(φ)
        double cosPhi = input.cosPhi().doubleValue();
        double sinPhi = Math.sqrt(1.0 - (cosPhi * cosPhi));
        BigDecimal sinPhiBd = BigDecimal.valueOf(sinPhi);
        trace.addStep("sin_phi", "Реактивная составляющая",
                "sinφ = √(1 − cosφ²)",
                "√(1 − " + fmt(input.cosPhi()) + "²)",
                fmt(sinPhiBd), "");

        // 5. Расчёт ΔU в Вольтах
        BigDecimal rPart = rEffective.multiply(input.cosPhi());
        BigDecimal xPart = xEffective.multiply(sinPhiBd);
        BigDecimal impedancePart = rPart.add(xPart);

        trace.addStep("impedance_part", "Импедансная составляющая",
                "Z = rэф·cosφ + xэф·sinφ",
                fmt(rEffective) + "·" + fmt(input.cosPhi()) + " + " + fmt(xEffective) + "·" + fmt(sinPhiBd),
                fmt(impedancePart), "мОм/км");

        BigDecimal deltaU = input.getSystemFactor()
                .multiply(input.current())
                .multiply(lengthKm)
                .multiply(impedancePart);

        String systemFactorStr = input.getSystemFactor().toString().equals("2")
                ? "2 (1-фаз)"
                : "√3 ≈ 1.732 (3-фаз)";
        trace.addStep("delta_u_volts", "Потеря напряжения",
                "ΔU = k·I·Lкм·Z",
                systemFactorStr + "·" + fmt(input.current()) + "·" + fmt(lengthKm) + "·" + fmt(impedancePart),
                fmt(deltaU), "В");

        // 6. Расчёт ΔU в % и конечного напряжения
        BigDecimal deltaUPct = deltaU
                .multiply(HUNDRED)
                .divide(input.networkVoltage(), 4, RoundingMode.HALF_UP);
        trace.addStep("delta_u_percent", "Потеря напряжения в процентах",
                "ΔU% = ΔU·100 / Uсети",
                fmt(deltaU) + "·100 / " + fmt(input.networkVoltage()),
                fmt(deltaUPct), "%");

        BigDecimal endV = input.networkVoltage().subtract(deltaU);
        trace.addStep("end_voltage", "Напряжение в конце линии",
                "Uконец = Uсети − ΔU",
                fmt(input.networkVoltage()) + " − " + fmt(deltaU),
                fmtVoltage(endV), "В");

        return new VoltageDropTrace(
                new VoltageDropResult(deltaU, deltaUPct, endV).roundTo(2),
                trace
        );
    }

    private String fmt(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    private String fmtVoltage(BigDecimal value) {
        return value.setScale(1, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}