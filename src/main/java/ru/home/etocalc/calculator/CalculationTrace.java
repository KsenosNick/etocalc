package ru.home.etocalc.calculator;

import ru.home.etocalc.api.dto.response.CalculationStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Собирает шаги расчёта.
 */
public class CalculationTrace {
    private final List<CalculationStep> steps = new ArrayList<>();
    private int nextOrder = 1;

    public void addStep(String key, String title, String formula,
                        String expression, String result, String unit) {
        steps.add(new CalculationStep(nextOrder++, key, title, formula,
                expression, result, unit));
    }

    public List<CalculationStep> getSteps() {
        return List.copyOf(steps);
    }
}