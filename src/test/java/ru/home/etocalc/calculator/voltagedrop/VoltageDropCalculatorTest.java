package ru.home.etocalc.calculator.voltagedrop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для калькулятора потерь напряжения.
 */
class VoltageDropCalculatorTest {

    private VoltageDropCalculator calculator;

    // Стандартные параметры для тестов
    private static final BigDecimal CABLE_NOMINAL_660 = new BigDecimal("660"); // Кабель на 660В
    private static final BigDecimal NETWORK_400 = new BigDecimal("400");       // 3-фазная сеть
    private static final BigDecimal NETWORK_230 = new BigDecimal("230");       // 1-фазная сеть
    private static final BigDecimal R_COPPER_2_5 = new BigDecimal("7.41");     // ВВГнг 3x2.5, мОм/м
    private static final BigDecimal X_CABLE = new BigDecimal("0.08");          // Ом/км
    private static final BigDecimal ALPHA_COPPER = new BigDecimal("0.00393");  // Медь
    private static final BigDecimal COS_PHI = new BigDecimal("0.9");

    @BeforeEach
    void setUp() {
        calculator = new VoltageDropCalculator();
    }

    // ==========================================
    // БАЗОВЫЕ РАСЧЁТЫ
    // ==========================================

    @Test
    @DisplayName("Трёхфазная сеть 400В: ВВГнг(А)-LS 3x2.5, 50м, 16А, cosφ=0.9, 20°C")
    void testThreePhase_StandardConditions() {
        VoltageDropInput input = new VoltageDropInput(
                NETWORK_400,          // Напряжение сети
                CABLE_NOMINAL_660,    // Номинал кабеля
                new BigDecimal("16"),
                new BigDecimal("50"),
                3, 1,
                R_COPPER_2_5,
                X_CABLE,
                COS_PHI,
                ALPHA_COPPER,
                AmbientCondition.standard()
        );

        VoltageDropResult result = calculator.calculate(input);

        assertNotNull(result);
        assertTrue(result.deltaUVolts().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.deltaUPercent().compareTo(BigDecimal.ZERO) > 0);
        // Конечное напряжение должно быть меньше НАПРЯЖЕНИЯ СЕТИ (400), а не номинала кабеля (660)
        assertTrue(result.endVoltage().compareTo(NETWORK_400) < 0,
                "Конечное напряжение должно быть меньше напряжения сети");
        assertTrue(result.endVoltage().compareTo(new BigDecimal("350")) > 0,
                "Конечное напряжение не должно падать слишком сильно");

        System.out.println("Трёхфазная сеть 400В (20°C):");
        System.out.println("  ΔU = " + result.deltaUVolts() + " В");
        System.out.println("  ΔU% = " + result.deltaUPercent() + " %");
        System.out.println("  U_конец = " + result.endVoltage() + " В");
    }

    @Test
    @DisplayName("Однофазная сеть 230В: ВВГнг(А)-LS 2x2.5, 50м, 16А, cosφ=0.9, 20°C")
    void testSinglePhase_StandardConditions() {
        VoltageDropInput input = new VoltageDropInput(
                NETWORK_230,          // Напряжение сети
                CABLE_NOMINAL_660,    // Номинал кабеля
                new BigDecimal("16"),
                new BigDecimal("50"),
                2, 1,                 // 2 проводника → 1-фазная
                R_COPPER_2_5,
                X_CABLE,
                COS_PHI,
                ALPHA_COPPER,
                AmbientCondition.standard()
        );

        VoltageDropResult result = calculator.calculate(input);

        assertNotNull(result);
        assertTrue(result.deltaUVolts().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.endVoltage().compareTo(NETWORK_230) < 0,
                "Конечное напряжение должно быть меньше 230В");

        System.out.println("Однофазная сеть 230В (20°C):");
        System.out.println("  ΔU = " + result.deltaUVolts() + " В");
        System.out.println("  ΔU% = " + result.deltaUPercent() + " %");
        System.out.println("  U_конец = " + result.endVoltage() + " В");
    }

    @Test
    @DisplayName("3-проводная сеть (L, N, PE) считается как однофазная с коэффициентом 2")
    void testThreeWireIsSinglePhase() {
        // 2 провода (L, N)
        VoltageDropInput input2Wire = createInputWithConductors(2);
        // 3 провода (L, N, PE)
        VoltageDropInput input3Wire = createInputWithConductors(3);

        VoltageDropResult result2 = calculator.calculate(input2Wire);
        VoltageDropResult result3 = calculator.calculate(input3Wire);

        // Потери должны быть одинаковыми, так как PE не несёт ток в нормальном режиме
        assertEquals(0, result2.deltaUVolts().compareTo(result3.deltaUVolts()),
                "2-проводная и 3-проводная сети должны давать одинаковые потери");
    }

    @Test
    @DisplayName("4- и 5-проводные сети считаются как трёхфазные с коэффициентом √3")
    void testFourAndFiveWireAreThreePhase() {
        VoltageDropInput input4Wire = createInputWithConductors(4);
        VoltageDropInput input5Wire = createInputWithConductors(5);

        VoltageDropResult result4 = calculator.calculate(input4Wire);
        VoltageDropResult result5 = calculator.calculate(input5Wire);

        assertEquals(0, result4.deltaUVolts().compareTo(result5.deltaUVolts()),
                "4-проводная и 5-проводная сети должны давать одинаковые потери");
    }

    // ==========================================
    // ВЛИЯНИЕ ПАРАМЕТРОВ
    // ==========================================

    @Test
    @DisplayName("Влияние температуры: при 40°C потери больше, чем при 20°C")
    void testTemperatureImpact() {
        VoltageDropInput input20 = createInputWithTemperature(new BigDecimal("20"));
        VoltageDropInput input40 = createInputWithTemperature(new BigDecimal("40"));

        VoltageDropResult result20 = calculator.calculate(input20);
        VoltageDropResult result40 = calculator.calculate(input40);

        assertTrue(result40.deltaUVolts().compareTo(result20.deltaUVolts()) > 0,
                "При 40°C потери напряжения должны быть больше, чем при 20°C");

        System.out.println("Влияние температуры:");
        System.out.println("  При 20°C: ΔU = " + result20.deltaUVolts() + " В");
        System.out.println("  При 40°C: ΔU = " + result40.deltaUVolts() + " В");
        System.out.println("  Разница: " + result40.deltaUVolts().subtract(result20.deltaUVolts()) + " В");
    }

    @Test
    @DisplayName("Влияние параллельных кабелей: 2 кабеля дают ~в 2 раза меньше потерь")
    void testParallelCables() {
        VoltageDropInput input1 = createInputWithParallelCables(1);
        VoltageDropInput input2 = createInputWithParallelCables(2);

        VoltageDropResult result1 = calculator.calculate(input1);
        VoltageDropResult result2 = calculator.calculate(input2);

        assertTrue(result2.deltaUVolts().compareTo(result1.deltaUVolts()) < 0,
                "При 2 параллельных кабелях потери должны быть меньше");

        BigDecimal ratio = result1.deltaUVolts().divide(result2.deltaUVolts(), 2, RoundingMode.HALF_UP);
        assertTrue(ratio.compareTo(new BigDecimal("1.9")) > 0 && ratio.compareTo(new BigDecimal("2.1")) < 0,
                "Потери должны уменьшиться примерно в 2 раза, фактически: " + ratio);

        System.out.println("Параллельные кабели:");
        System.out.println("  1 кабель: ΔU = " + result1.deltaUVolts() + " В");
        System.out.println("  2 кабеля: ΔU = " + result2.deltaUVolts() + " В");
        System.out.println("  Отношение: " + ratio);
    }

    // ==========================================
    // ПРОВЕРКА ИСПРАВЛЕННОГО БАГА
    // ==========================================

    @Test
    @DisplayName("ΔU% и U_конец считаются от напряжения сети, а не от номинала кабеля")
    void testPercentAndEndVoltageUseNetworkVoltage() {
        // Кабель на 660В работает в сети 400В
        VoltageDropInput input = new VoltageDropInput(
                NETWORK_400,          // Сеть 400В
                CABLE_NOMINAL_660,    // Кабель 660В
                new BigDecimal("16"),
                new BigDecimal("50"),
                3, 1,
                R_COPPER_2_5,
                X_CABLE,
                COS_PHI,
                ALPHA_COPPER,
                AmbientCondition.standard()
        );

        VoltageDropResult result = calculator.calculate(input);

        // Проверяем, что ΔU% = (ΔU / 400) * 100, а не (ΔU / 660) * 100
        BigDecimal expectedPercent = result.deltaUVolts()
                .multiply(new BigDecimal("100"))
                .divide(NETWORK_400, 2, RoundingMode.HALF_UP);
        assertEquals(0, expectedPercent.compareTo(result.deltaUPercent()),
                "ΔU% должен считаться от напряжения сети (400В)");

        // Проверяем, что U_конец = 400 - ΔU, а не 660 - ΔU
        BigDecimal expectedEndVoltage = NETWORK_400.subtract(result.deltaUVolts())
                .setScale(2, RoundingMode.HALF_UP);
        assertEquals(0, expectedEndVoltage.compareTo(result.endVoltage()),
                "U_конец должен считаться от напряжения сети (400В)");

        System.out.println("Проверка исправленного бага:");
        System.out.println("  ΔU = " + result.deltaUVolts() + " В");
        System.out.println("  ΔU% = " + result.deltaUPercent() + " % (от 400В, не от 660В)");
        System.out.println("  U_конец = " + result.endVoltage() + " В (400 - ΔU, не 660 - ΔU)");
    }

    // ==========================================
    // ВАЛИДАЦИЯ
    // ==========================================

    @Test
    @DisplayName("Валидация: напряжение сети не может превышать номинал кабеля")
    void testNetworkVoltageExceedsCableNominal() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            new VoltageDropInput(
                    new BigDecimal("690"),      // ❌ Сеть 690В
                    CABLE_NOMINAL_660,          // Кабель 660В
                    new BigDecimal("16"),
                    new BigDecimal("50"),
                    3, 1,
                    R_COPPER_2_5,
                    X_CABLE,
                    COS_PHI,
                    ALPHA_COPPER,
                    AmbientCondition.standard()
            );
        });
        assertTrue(ex.getMessage().contains("не может превышать"),
                "Сообщение должно объяснять причину ошибки");
    }

    @Test
    @DisplayName("Валидация: напряжение сети равное номиналу кабеля допустимо (граничный случай)")
    void testNetworkVoltageEqualsCableNominal() {
        assertDoesNotThrow(() -> {
            new VoltageDropInput(
                    CABLE_NOMINAL_660,      // ✅ Сеть = номинал кабеля
                    CABLE_NOMINAL_660,
                    new BigDecimal("16"),
                    new BigDecimal("50"),
                    3, 1,
                    R_COPPER_2_5,
                    X_CABLE,
                    COS_PHI,
                    ALPHA_COPPER,
                    AmbientCondition.standard()
            );
        });
    }

    @Test
    @DisplayName("Валидация: некорректное количество проводников")
    void testInvalidConductorCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VoltageDropInput(
                    NETWORK_400,
                    CABLE_NOMINAL_660,
                    new BigDecimal("16"),
                    new BigDecimal("50"),
                    1,  // ❌ Должно быть >= 2
                    1,
                    R_COPPER_2_5,
                    X_CABLE,
                    COS_PHI,
                    ALPHA_COPPER,
                    AmbientCondition.standard()
            );
        });
    }

    @Test
    @DisplayName("Валидация: некорректный cosφ (> 1)")
    void testInvalidCosPhi() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VoltageDropInput(
                    NETWORK_400,
                    CABLE_NOMINAL_660,
                    new BigDecimal("16"),
                    new BigDecimal("50"),
                    3, 1,
                    R_COPPER_2_5,
                    X_CABLE,
                    new BigDecimal("1.5"),  // ❌ Должно быть <= 1
                    ALPHA_COPPER,
                    AmbientCondition.standard()
            );
        });
    }

    @Test
    @DisplayName("Валидация: некорректное количество параллельных кабелей (< 1)")
    void testInvalidParallelCableCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VoltageDropInput(
                    NETWORK_400,
                    CABLE_NOMINAL_660,
                    new BigDecimal("16"),
                    new BigDecimal("50"),
                    3,
                    0,  // ❌ Должно быть >= 1
                    R_COPPER_2_5,
                    X_CABLE,
                    COS_PHI,
                    ALPHA_COPPER,
                    AmbientCondition.standard()
            );
        });
    }

    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ==========================================

    private VoltageDropInput createInputWithConductors(int conductorCount) {
        return new VoltageDropInput(
                NETWORK_400,
                CABLE_NOMINAL_660,
                new BigDecimal("16"),
                new BigDecimal("50"),
                conductorCount,
                1,
                R_COPPER_2_5,
                X_CABLE,
                COS_PHI,
                ALPHA_COPPER,
                AmbientCondition.standard()
        );
    }

    private VoltageDropInput createInputWithTemperature(BigDecimal temperature) {
        return new VoltageDropInput(
                NETWORK_400,
                CABLE_NOMINAL_660,
                new BigDecimal("16"),
                new BigDecimal("50"),
                3, 1,
                R_COPPER_2_5,
                X_CABLE,
                COS_PHI,
                ALPHA_COPPER,
                new AmbientCondition(temperature)
        );
    }

    private VoltageDropInput createInputWithParallelCables(int parallelCables) {
        return new VoltageDropInput(
                NETWORK_400,
                CABLE_NOMINAL_660,
                new BigDecimal("32"),
                new BigDecimal("100"),
                3,
                parallelCables,
                new BigDecimal("3.08"),  // R для 6мм²
                X_CABLE,
                COS_PHI,
                ALPHA_COPPER,
                AmbientCondition.standard()
        );
    }
}