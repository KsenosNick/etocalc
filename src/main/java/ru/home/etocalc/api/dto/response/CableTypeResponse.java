package ru.home.etocalc.api.dto.response;

public record CableTypeResponse(
        Long id,
        String name,
        String conductorMaterialName,
        String insulationMaterialName,
        String sheathMaterialName,
        Boolean hasArmor
) {
    public static CableTypeResponse from(ru.home.etocalc.domain.model.catalog.CableType ct) {
        return new CableTypeResponse(
                ct.getId(),
                ct.getName(),
                ct.getConductorMaterial().getName(),
                ct.getInsulationMaterial().getName(),
                ct.getSheathMaterial().getName(),
                ct.getHasArmor()
        );
    }
}