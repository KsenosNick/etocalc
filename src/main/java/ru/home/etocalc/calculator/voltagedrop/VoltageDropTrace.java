package ru.home.etocalc.calculator.voltagedrop;

import ru.home.etocalc.calculator.CalculationTrace;

/**
 * Результат расчёта с историей вычислений.
 */
public record VoltageDropTrace(
        VoltageDropResult result,
        CalculationTrace trace
) {}