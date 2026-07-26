-- ========================================
-- 1. МАТЕРИАЛЫ (Базовая иерархия)
-- ========================================

CREATE TABLE materials (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    material_type VARCHAR(20) NOT NULL
        CHECK (material_type IN ('CONDUCTOR', 'INSULATION', 'SHEATH')),
    CONSTRAINT uq_material_name_type UNIQUE (name, material_type)  -- ✅ Составная уникальность
);

CREATE TABLE conductor_materials (
    id BIGINT PRIMARY KEY REFERENCES materials(id),
    resistivity NUMERIC(10, 6) NOT NULL
);

CREATE TABLE insulation_materials (
    id BIGINT PRIMARY KEY REFERENCES materials(id),
    temp_normal INTEGER NOT NULL,
    temp_overload INTEGER NOT NULL CHECK (temp_overload > temp_normal),
    temp_short_circuit INTEGER NOT NULL CHECK (temp_short_circuit > temp_overload),
    temp_ignition INTEGER NOT NULL CHECK (temp_ignition > temp_short_circuit)
);

CREATE TABLE sheath_materials (
    id BIGINT PRIMARY KEY REFERENCES materials(id)
);

-- ========================================
-- 2. КАТАЛОГ ТИПОВ КАБЕЛЕЙ
-- ========================================

CREATE TABLE catalog_cable_types (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    conductor_material_id BIGINT NOT NULL REFERENCES conductor_materials(id),
    insulation_material_id BIGINT NOT NULL REFERENCES insulation_materials(id),
    sheath_material_id BIGINT NOT NULL REFERENCES sheath_materials(id),
    has_armor BOOLEAN NOT NULL DEFAULT false
);

-- ========================================
-- 3. КАТАЛОГ КОНКРЕТНЫХ КАБЕЛЕЙ (СПЕЦИФИКАЦИЙ)
-- ========================================

CREATE TABLE catalog_cables (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cable_type_id BIGINT NOT NULL REFERENCES catalog_cable_types(id),
    nominal_voltage INTEGER NOT NULL,
    conductor_count INTEGER NOT NULL,
    cross_section NUMERIC(10, 2) NOT NULL,
    r_ph_active NUMERIC(10, 6) NOT NULL,
    r0_ph_active NUMERIC(10, 6) NOT NULL,
    x_ph_reactive NUMERIC(10, 6) NOT NULL,
    x0_reactive NUMERIC(10, 6) NOT NULL,
    max_current_earth INTEGER NOT NULL,
    max_current_air INTEGER NOT NULL
);

-- Индексы для ускорения поиска
CREATE INDEX idx_cables_type ON catalog_cables(cable_type_id);
CREATE INDEX idx_cable_types_conductor ON catalog_cable_types(conductor_material_id);