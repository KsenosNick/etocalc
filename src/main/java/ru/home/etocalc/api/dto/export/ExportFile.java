package ru.home.etocalc.api.dto.export;

import java.time.Instant;
import java.util.List;

/**
 * Универсальный контейнер файла экспорта.
 * Метаданные позволяют при импорте проверить совместимость формата и тип данных.
 *
 * @param entityType Тип содержимого (CABLE, TRANSFORMER, BREAKER...)
 * @param formatVersion Версия формата — на случай будущих изменений структуры
 * @param exportedAt Момент выгрузки (информационно)
 * @param items Полезная нагрузка
 */
public record ExportFile<T>(
        String entityType,
        int formatVersion,
        Instant exportedAt,
        List<T> items
) {
    public static final int CURRENT_VERSION = 1;

    public static <T> ExportFile<T> of(String entityType, List<T> items) {
        return new ExportFile<>(entityType, CURRENT_VERSION, Instant.now(), items);
    }
}