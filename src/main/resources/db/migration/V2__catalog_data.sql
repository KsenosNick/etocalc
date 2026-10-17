-- ========================================
-- 1. ПРОВОДНИКИ
-- ========================================
INSERT INTO materials (name, material_type) VALUES
('Медь', 'CONDUCTOR'),
('Алюминий', 'CONDUCTOR');

INSERT INTO conductor_materials (id, resistivity)
SELECT id,
       CASE name
       WHEN 'Медь' THEN 0.0175
       WHEN 'Алюминий' THEN 0.0280
       END
FROM materials
WHERE material_type = 'CONDUCTOR';

-- ========================================
-- 2. ИЗОЛЯЦИЯ
-- ========================================
INSERT INTO materials (name, material_type) VALUES
('ПВХ пластикат', 'INSULATION'),
('ПВХ пластикат пониженной пожарной опасности', 'INSULATION'),
('Сшитый полиэтилен', 'INSULATION');

INSERT INTO insulation_materials (id, temp_normal, temp_overload, temp_short_circuit, temp_ignition)
SELECT id,
       CASE name WHEN 'Сшитый полиэтилен' THEN 90  ELSE 70  END,
       CASE name WHEN 'Сшитый полиэтилен' THEN 130 ELSE 90  END,
       CASE name WHEN 'Сшитый полиэтилен' THEN 250 ELSE 160 END,
       CASE name WHEN 'Сшитый полиэтилен' THEN 400 ELSE 350 END
FROM materials
WHERE material_type = 'INSULATION';

-- ========================================
-- 3. ОБОЛОЧКА
-- ========================================
INSERT INTO materials (name, material_type) VALUES
('ПВХ пластикат', 'SHEATH'),
('ПВХ пластикат пониженной пожарной опасности', 'SHEATH'),
('Сшитый полиэтилен', 'SHEATH');

INSERT INTO sheath_materials (id)
SELECT id
FROM materials
WHERE material_type = 'SHEATH';

-- ========================================
-- 4. ТИП КАБЕЛЯ: ВВГнг(А)-LS
-- ========================================
INSERT INTO catalog_cable_types (name, conductor_material_id, insulation_material_id, sheath_material_id, has_armor)
SELECT
    'ВВГнг(А)-LS',
    (SELECT id FROM materials WHERE name = 'Медь' AND material_type = 'CONDUCTOR'),
    (SELECT id FROM materials WHERE name = 'ПВХ пластикат пониженной пожарной опасности' AND material_type = 'INSULATION'),
    (SELECT id FROM materials WHERE name = 'ПВХ пластикат пониженной пожарной опасности' AND material_type = 'SHEATH'),
    false;

-- ========================================
-- 5. СПЕЦИФИКАЦИИ КАБЕЛЕЙ ВВГнг(А)-LS
-- ========================================
INSERT INTO catalog_cables (
    cable_type_id, nominal_voltage, conductor_count, cross_section,
    r_ph_active, r0_ph_active, x_ph_reactive, x0_reactive,
    max_current_air, max_current_earth
)
SELECT
    (SELECT id FROM catalog_cable_types WHERE name = 'ВВГнг(А)-LS'),
    v.voltage, v.cores, v.section,
    v.r_ph, v.r0_ph, v.x_ph, v.x0_ph,
    v.i_air, v.i_earth
FROM (VALUES
    (660, 3, 1.5,  12.100, 12.100, 0.090, 0.090,  21,  27),
    (660, 3, 2.5,   7.410,  7.410, 0.080, 0.080,  27,  36),
    (660, 3, 4.0,   4.610,  4.610, 0.080, 0.080,  36,  47),
    (660, 3, 6.0,   3.080,  3.080, 0.080, 0.080,  46,  59),
    (660, 3, 10.0,  1.830,  1.830, 0.078, 0.078,  63,  79),
    (660, 3, 16.0,  1.150,  1.150, 0.078, 0.078,  84, 102),
    (660, 5, 1.5,  12.100, 12.100, 0.090, 0.090,  19,  25),
    (660, 5, 2.5,   7.410,  7.410, 0.080, 0.080,  25,  33),
    (660, 5, 4.0,   4.610,  4.610, 0.080, 0.080,  33,  44),
    (660, 5, 6.0,   3.080,  3.080, 0.080, 0.080,  43,  55),
    (660, 5, 10.0,  1.830,  1.830, 0.078, 0.078,  59,  73),
    (660, 5, 16.0,  1.150,  1.150, 0.078, 0.078,  78,  95),
    (660, 5, 25.0,  0.727,  0.727, 0.078, 0.078, 104, 124),
    (660, 5, 35.0,  0.524,  0.524, 0.078, 0.078, 127, 147),
    (660, 5, 50.0,  0.387,  0.387, 0.075, 0.075, 155, 174),
    (1000, 5, 70.0,  0.268,  0.268, 0.075, 0.075, 196, 215),
    (1000, 5, 95.0,  0.193,  0.193, 0.075, 0.075, 243, 259),
    (1000, 5, 120.0, 0.153,  0.153, 0.075, 0.075, 281, 295)
) AS v(voltage, cores, section, r_ph, r0_ph, x_ph, x0_ph, i_air, i_earth);