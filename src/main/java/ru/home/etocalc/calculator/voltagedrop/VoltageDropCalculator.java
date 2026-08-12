package ru.home.etocalc.calculator.voltagedrop;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class VoltageDropCalculator {

    private static final BigDecimal TWENTY = new BigDecimal("20");
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public VoltageDropResult calculate(VoltageDropInput input) {
        // 1. Корректировка сопротивления по температуре
        BigDecimal tempDelta = input.ambientCondition().temperature().subtract(TWENTY);
        BigDecimal tempFactor = BigDecimal.ONE.add(input.temperatureCoefficient().multiply(tempDelta));
        BigDecimal rPhActiveAdjusted = input.rPhActive().multiply(tempFactor);

        // 2. Учёт параллельных кабелей
        BigDecimal parallelCount = new BigDecimal(input.parallelCableCount());
        BigDecimal rEffective = rPhActiveAdjusted.divide(parallelCount, MathContext.DECIMAL64);
        BigDecimal xEffective = input.xPhReactive().divide(parallelCount, MathContext.DECIMAL64);

        // 3. Единицы измерения
        BigDecimal lengthKm = input.length().divide(THOUSAND, MathContext.DECIMAL64);

        // 4. sin(φ)
        double cosPhi = input.cosPhi().doubleValue();
        double sinPhi = Math.sqrt(1.0 - (cosPhi * cosPhi));
        BigDecimal sinPhiBd = BigDecimal.valueOf(sinPhi);

        // 5. Расчёт ΔU в Вольтах
        BigDecimal rPart = rEffective.multiply(input.cosPhi());
        BigDecimal xPart = xEffective.multiply(sinPhiBd);
        BigDecimal impedancePart = rPart.add(xPart);

        BigDecimal deltaU = input.getSystemFactor()
                .multiply(input.current())
                .multiply(lengthKm)
                .multiply(impedancePart);

        // 6. Расчёт ΔU в % и конечного напряжения ОТНОСИТЕЛЬНО НАПРЯЖЕНИЯ СЕТИ
        BigDecimal deltaUPct = deltaU
                .multiply(HUNDRED)
                .divide(input.networkVoltage(), 4, RoundingMode.HALF_UP);

        BigDecimal endV = input.networkVoltage().subtract(deltaU);

        return new VoltageDropResult(deltaU, deltaUPct, endV).roundTo(2);
    }
}