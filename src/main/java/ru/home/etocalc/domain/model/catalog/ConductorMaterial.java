package ru.home.etocalc.domain.model.catalog;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Материал токопроводящей жилы (медь, алюминий и т.д.)
 */
@Entity
@Table(name = "conductor_materials")
@DiscriminatorValue("CONDUCTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConductorMaterial extends Material {

    /** Удельное электрическое сопротивление, Ом·мм²/м */
    @Column(name = "resistivity", nullable = false, precision = 10, scale = 6)
    private BigDecimal resistivity;
}