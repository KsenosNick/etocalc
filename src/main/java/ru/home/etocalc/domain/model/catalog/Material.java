package ru.home.etocalc.domain.model.catalog;

import jakarta.persistence.*;
import lombok.*;

/**
 * Абстрактный базовый класс для всех материалов.
 * Использует стратегию наследования JOINED - каждый подкласс имеет свою таблицу.
 */
@Entity
@Table(name = "materials")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "material_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}