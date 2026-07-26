package ru.home.etocalc.domain.model.catalog;

import jakarta.persistence.*;
import lombok.*;

/**
 * Материал оболочки кабеля (ПВХ, полиэтилен и т.д.)
 */
@Entity
@Table(name = "sheath_materials")
@DiscriminatorValue("SHEATH")
@Getter
@Setter
@NoArgsConstructor
public class SheathMaterial extends Material {
    // Пока только name из базового класса.
    // В будущем можно добавить: стойкость к УФ, химическую стойкость и т.д.
}