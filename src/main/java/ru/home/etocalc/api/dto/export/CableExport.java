package ru.home.etocalc.api.dto.export;

import java.math.BigDecimal;

/**
 * Кабель в формате экспорта/импорта.
 * Ссылка на тип даётся по имени, а не по id — файл должен переноситься между базами.
 */
public record CableExport(
        String cableTypeName,
        Integer nominalVoltage,
        Integer conductorCount,
        BigDecimal crossSection,
        BigDecimal rPhActive,
        BigDecimal r0PhActive,
        BigDecimal xPhReactive,
        BigDecimal x0Reactive,
        Integer maxCurrentEarth,
        Integer maxCurrentAir
) {}