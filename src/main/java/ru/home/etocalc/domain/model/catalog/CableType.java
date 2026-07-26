package ru.home.etocalc.domain.model.catalog;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "catalog_cable_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CableType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conductor_material_id", nullable = false)
    private ConductorMaterial conductorMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insulation_material_id", nullable = false)
    private InsulationMaterial insulationMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheath_material_id", nullable = false)
    private SheathMaterial sheathMaterial;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasArmor = false;
}