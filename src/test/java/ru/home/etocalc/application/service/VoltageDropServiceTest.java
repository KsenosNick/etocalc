package ru.home.etocalc.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.home.etocalc.api.dto.request.CalculationSourceType;
import ru.home.etocalc.api.dto.request.VoltageDropRequest;
import ru.home.etocalc.api.dto.response.VoltageDropResponse;
import ru.home.etocalc.calculator.CalculationTrace;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropCalculator;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropInput;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropResult;
import ru.home.etocalc.calculator.voltagedrop.VoltageDropTrace;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.domain.model.catalog.CableType;
import ru.home.etocalc.domain.model.catalog.ConductorMaterial;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoltageDropServiceTest {

    @Mock
    private CableRepository cableRepository;

    @Mock
    private VoltageDropCalculator calculator;

    @InjectMocks
    private VoltageDropService voltageDropService;

    // ==========================================================
    // ХЕЛПЕРЫ: заглушки создаются ТОЛЬКО там, где они нужны
    // ==========================================================

    /** Настоящий record вместо мока — никаких лишних заглушек. */
    private VoltageDropRequest request(CalculationSourceType type,
                                       Double networkVoltage,
                                       Double current,
                                       Double power) {
        return new VoltageDropRequest(
                type,
                1L,
                networkVoltage,
                networkVoltage,   // targetVoltage
                current,
                power,
                50.0,
                0.9,
                20.0,
                1
        );
    }

    /** Полный мок кабеля: используется только в "зелёных" сценариях. */
    private Cable givenCable() {
        ConductorMaterial material = mock(ConductorMaterial.class);
        when(material.getName()).thenReturn("Медь");

        CableType type = mock(CableType.class);
        when(type.getConductorMaterial()).thenReturn(material);

        Cable cable = mock(Cable.class);
        when(cable.getNominalVoltage()).thenReturn(660);
        when(cable.getCableType()).thenReturn(type);
        when(cable.getConductorCount()).thenReturn(3);
        when(cable.getRPhActive()).thenReturn(new BigDecimal("7.41"));
        when(cable.getXPhReactive()).thenReturn(new BigDecimal("0.08"));
        when(cable.getFullName()).thenReturn("ВВГнг(А)-LS 3x2.5");

        when(cableRepository.findByIdWithAllMaterials(1L)).thenReturn(Optional.of(cable));
        return cable;
    }

    /** Мок результата калькулятора: только для успешных сценариев. */
    private void givenCalculatorResult() {
        VoltageDropResult result = mock(VoltageDropResult.class);
        when(result.deltaUVolts()).thenReturn(new BigDecimal("5.50"));
        when(result.deltaUPercent()).thenReturn(new BigDecimal("1.375"));
        when(result.endVoltage()).thenReturn(new BigDecimal("394.50"));

        CalculationTrace trace = mock(CalculationTrace.class);
        when(trace.getSteps()).thenReturn(List.of());

        VoltageDropTrace traceResult = mock(VoltageDropTrace.class);
        when(traceResult.result()).thenReturn(result);
        when(traceResult.trace()).thenReturn(trace);

        when(calculator.calculateWithTrace(any(VoltageDropInput.class)))
                .thenReturn(traceResult);
    }

    // ==========================================================
    // 1. УСПЕШНЫЕ СЦЕНАРИИ
    // ==========================================================

    @Test
    @DisplayName("Успешный расчёт с явно заданным током (3-фазная сеть)")
    void testCalculate_WithExplicitCurrent_Success() {
        givenCable();
        givenCalculatorResult();

        VoltageDropResponse response = voltageDropService.calculate(
                request(CalculationSourceType.CABLE, 400.0, 16.0, null)
        );

        assertNotNull(response);
        assertEquals("ВВГнг(А)-LS 3x2.5", response.cableName());
        assertEquals(5.50, response.deltaUVolts());
        assertEquals(394.50, response.endVoltage());

        ArgumentCaptor<VoltageDropInput> captor =
                ArgumentCaptor.forClass(VoltageDropInput.class);
        verify(calculator).calculateWithTrace(captor.capture());
        assertEquals(0, captor.getValue().current()
                .compareTo(new BigDecimal("16.0")));
    }

    @Test
    @DisplayName("Расчёт тока из мощности для 3-фазной сети (U >= 380В)")
    void testCalculate_CurrentFromPower_ThreePhase() {
        givenCable();
        givenCalculatorResult();

        voltageDropService.calculate(
                request(CalculationSourceType.CABLE, 400.0, 1.0, 10.0)
        );

        ArgumentCaptor<VoltageDropInput> captor =
                ArgumentCaptor.forClass(VoltageDropInput.class);
        verify(calculator).calculateWithTrace(captor.capture());

        // I = 10 / (√3 * 0.4 * 0.9) ≈ 16.0375 А
        double actual = captor.getValue().current().doubleValue();
        assertTrue(Math.abs(actual - 16.0375) < 0.01,
                "Ожидаемый ток ~16.04 А, фактический: " + actual);
    }

    @Test
    @DisplayName("Расчёт тока из мощности для 1-фазной сети (U < 380В)")
    void testCalculate_CurrentFromPower_SinglePhase() {
        givenCable();
        givenCalculatorResult();

        voltageDropService.calculate(
                request(CalculationSourceType.CABLE, 230.0, 1.0, 3.0)
        );

        ArgumentCaptor<VoltageDropInput> captor =
                ArgumentCaptor.forClass(VoltageDropInput.class);
        verify(calculator).calculateWithTrace(captor.capture());

        // I = 3 / (0.230 * 0.9) ≈ 14.4928 А
        double actual = captor.getValue().current().doubleValue();
        assertTrue(Math.abs(actual - 14.4928) < 0.01,
                "Ожидаемый ток ~14.49 А, фактический: " + actual);
    }

    // ==========================================================
    // 2. НЕГАТИВНЫЕ СЦЕНАРИИ (минимум заглушек!)
    // ==========================================================

    @Test
    @DisplayName("Ошибка: напряжение сети превышает номинал кабеля")
    void testCalculate_NetworkVoltageExceedsCableNominal() {
        Cable cable = mock(Cable.class);
        when(cable.getNominalVoltage()).thenReturn(230);
        when(cableRepository.findByIdWithAllMaterials(1L))
                .thenReturn(Optional.of(cable));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voltageDropService.calculate(
                        request(CalculationSourceType.CABLE, 400.0, 16.0, null))
        );

        assertTrue(ex.getMessage().contains("превышает номинал кабеля"));
        verifyNoInteractions(calculator);
    }

    @Test
    @DisplayName("Ошибка: кабель с указанным ID не найден")
    void testCalculate_CableNotFound() {
        when(cableRepository.findByIdWithAllMaterials(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> voltageDropService.calculate(
                        request(CalculationSourceType.CABLE, 400.0, 16.0, null))
        );

        assertTrue(ex.getMessage().contains("не найден"));
        verifyNoInteractions(calculator);
    }

    @Test
    @DisplayName("Ошибка: неподдерживаемый тип источника (шинопровод)")
    void testCalculate_UnsupportedSourceType() {
        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> voltageDropService.calculate(
                        request(CalculationSourceType.BUSBAR, 400.0, 16.0, null))
        );

        assertTrue(ex.getMessage().contains("пока не реализован"));
        verifyNoInteractions(cableRepository);
        verifyNoInteractions(calculator);
    }
}