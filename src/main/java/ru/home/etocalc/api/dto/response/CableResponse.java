package ru.home.etocalc.api.dto.response;

import ru.home.etocalc.domain.model.catalog.Cable;

import java.math.BigDecimal;

public record CableResponse(
        Long id,
        Long cableTypeId,
        String cableTypeName,          // Название типа (например, "ВВГнг(А)-LS")
        String conductorMaterialName,  // Название материала жилы (например, "Медь")
        Integer nominalVoltage,
        Integer conductorCount,
        BigDecimal crossSection,
        BigDecimal rPhActive,
        BigDecimal r0PhActive,
        BigDecimal xPhReactive,
        BigDecimal x0Reactive,
        Integer maxCurrentEarth,
        Integer maxCurrentAir,
        String fullName                // Наш вычисляемый геттер
) {
    // Статический метод для удобного маппинга из Entity
    public static CableResponse from(Cable cable) {
        return new CableResponse(
                cable.getId(),
                cable.getCableType().getId(),
                cable.getCableType().getName(),
                cable.getCableType().getConductorMaterial().getName(),
                cable.getNominalVoltage(),
                cable.getConductorCount(),
                cable.getCrossSection(),
                cable.getRPhActive(),
                cable.getR0PhActive(),
                cable.getXPhReactive(),
                cable.getX0Reactive(),
                cable.getMaxCurrentEarth(),
                cable.getMaxCurrentAir(),
                cable.getFullName()
        );
    }
}