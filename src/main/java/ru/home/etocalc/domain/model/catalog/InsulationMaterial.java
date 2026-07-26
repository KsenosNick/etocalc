package ru.home.etocalc.domain.model.catalog;

import jakarta.persistence.*;
import lombok.*;

/**
 * Материал изоляции кабеля (ПВХ, сшитый полиэтилен и т.д.)
 */
@Entity
@Table(name = "insulation_materials")
@DiscriminatorValue("INSULATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsulationMaterial extends Material {

    /** Допустимая температура жилы в нормальном режиме, °C */
    @Column(name = "temp_normal", nullable = false)
    private Integer tempNormal;

    /** Допустимая температура жилы в режиме перегрузки, °C */
    @Column(name = "temp_overload", nullable = false)
    private Integer tempOverload;

    /** Допустимая температура жилы при коротком замыкании, °C */
    @Column(name = "temp_short_circuit", nullable = false)
    private Integer tempShortCircuit;

    /** Температура возгорания, °C */
    @Column(name = "temp_ignition", nullable = false)
    private Integer tempIgnition;
}