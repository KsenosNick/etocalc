package ru.home.etocalc.domain.model.catalog;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalog_cables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cable_type_id", nullable = false)
    private CableType cableType;

    @Column(name = "nominal_voltage", nullable = false)
    private Integer nominalVoltage; // Номинальное напряжение, В

    @Column(name = "conductor_count", nullable = false)
    private Integer conductorCount; // Количество жил

    @Column(name = "cross_section", nullable = false, precision = 10, scale = 2)
    private BigDecimal crossSection; // Сечение в мм2

    // ЯВНО указываем имена колонок, чтобы они на 100% совпадали с SQL-скриптом
    @Column(name = "r_ph_active", nullable = false, precision = 10, scale = 6)
    private BigDecimal rPhActive;

    @Column(name = "r0_ph_active", nullable = false, precision = 10, scale = 6)
    private BigDecimal r0PhActive;

    @Column(name = "x_ph_reactive", nullable = false, precision = 10, scale = 6)
    private BigDecimal xPhReactive;

    @Column(name = "x0_reactive", nullable = false, precision = 10, scale = 6)
    private BigDecimal x0Reactive;

    @Column(name = "max_current_earth", nullable = false)
    private Integer maxCurrentEarth;

    @Column(name = "max_current_air", nullable = false)
    private Integer maxCurrentAir;

    @Transient
    public String getFullName() {
        if (this.cableType == null) {
            return "Неизвестный кабель";
        }
        return this.cableType.getName() + " " + this.conductorCount + "x" + this.crossSection + "-" + (this.nominalVoltage / 1000.0);
    }
}