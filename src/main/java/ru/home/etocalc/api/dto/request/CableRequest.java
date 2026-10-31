package ru.home.etocalc.api.dto.request;

import java.math.BigDecimal;

public record CableRequest(
        Long cableTypeId,
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