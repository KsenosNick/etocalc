package ru.home.etocalc.api.dto.response;

/**
 * Один шаг промежуточного расчёта.
 */
public record CalculationStep(
        int order,          // Порядковый номер
        String key,         // Машинный ключ
        String title,       // Человекочитаемое название
        String formula,     // Формула в общем виде
        String expression,  // Формула с подставленными значениями
        String result,      // Результат (уже отформатированный)
        String unit         // Единица измерения (может быть пустой)
) {}