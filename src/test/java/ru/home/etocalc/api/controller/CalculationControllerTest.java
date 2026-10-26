package ru.home.etocalc.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.home.etocalc.api.dto.request.CalculationSourceType;
import ru.home.etocalc.api.dto.request.VoltageDropRequest;
import ru.home.etocalc.api.dto.response.VoltageDropResponse;
import ru.home.etocalc.application.service.VoltageDropService;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculationController.class)
@Import(CalculationControllerTest.TestGlobalExceptionHandler.class) // ✅ ЯВНО ПОДКЛЮЧАЕМ ОБРАБОТЧИК
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VoltageDropService voltageDropService;

    @MockBean
    private CableRepository cableRepository;

    // ==========================================================
    // Глобальный обработчик исключений ТОЛЬКО для этого теста
    // ==========================================================
    @RestControllerAdvice
    static class TestGlobalExceptionHandler {
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }

        @ExceptionHandler(UnsupportedOperationException.class)
        @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
        public ResponseEntity<Map<String, String>> handleUnsupportedOperation(UnsupportedOperationException ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("message", ex.getMessage()));
        }
    }

    // ==========================================================
    // 1. УСПЕШНЫЙ РАСЧЁТ (POST)
    // ==========================================================

    @Test
    @DisplayName("POST /api/calculation/voltage-drop — успешный расчёт")
    void testCalculateVoltageDrop_Success() throws Exception {
        VoltageDropResponse mockResponse = new VoltageDropResponse(
                "ВВГнг(А)-LS 3x2.5",
                5.50,
                1.375,
                394.50,
                400.0,
                List.of()
        );
        when(voltageDropService.calculate(any(VoltageDropRequest.class))).thenReturn(mockResponse);

        VoltageDropRequest request = new VoltageDropRequest(
                CalculationSourceType.CABLE,
                1L,
                400.0,
                400.0,
                16.0,
                null,
                50.0,
                0.9,
                20.0,
                1
        );

        mockMvc.perform(post("/api/calculation/voltage-drop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cableName").value("ВВГнг(А)-LS 3x2.5"))
                .andExpect(jsonPath("$.deltaUVolts").value(5.50))
                .andExpect(jsonPath("$.endVoltage").value(394.50));
    }

    // ==========================================================
    // 2. ВАЛИДАЦИЯ ВХОДНЫХ ДАННЫХ (400 Bad Request)
    // ==========================================================

    @Test
    @DisplayName("POST /api/calculation/voltage-drop — ошибка валидации: networkVoltage < 10")
    void testCalculateVoltageDrop_InvalidNetworkVoltage() throws Exception {
        VoltageDropRequest request = new VoltageDropRequest(
                CalculationSourceType.CABLE,
                1L,
                5.0,
                400.0,
                16.0,
                null,
                50.0,
                0.9,
                20.0,
                1
        );

        mockMvc.perform(post("/api/calculation/voltage-drop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/calculation/voltage-drop — ошибка валидации: cosPhi > 1")
    void testCalculateVoltageDrop_InvalidCosPhi() throws Exception {
        VoltageDropRequest request = new VoltageDropRequest(
                CalculationSourceType.CABLE,
                1L,
                400.0,
                400.0,
                16.0,
                null,
                50.0,
                1.5,
                20.0,
                1
        );

        mockMvc.perform(post("/api/calculation/voltage-drop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================================
    // 3. ОБРАБОТКА ИСКЛЮЧЕНИЙ СЕРВИСА
    // ==========================================================

    @Test
    @DisplayName("POST /api/calculation/voltage-drop — сервис выбросил IllegalArgumentException")
    void testCalculateVoltageDrop_ServiceThrowsIllegalArgument() throws Exception {
        when(voltageDropService.calculate(any(VoltageDropRequest.class)))
                .thenThrow(new IllegalArgumentException("Напряжение сети превышает номинал кабеля"));

        VoltageDropRequest request = new VoltageDropRequest(
                CalculationSourceType.CABLE,
                1L,
                400.0,
                400.0,
                16.0,
                null,
                50.0,
                0.9,
                20.0,
                1
        );

        mockMvc.perform(post("/api/calculation/voltage-drop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Напряжение сети превышает номинал кабеля"));
    }

    // ==========================================================
    // 4. ДЕМО-ЭНДПОИНТ (GET)
    // ==========================================================

    @Test
    @DisplayName("GET /api/calculation/voltage-drop/demo — успешный демо-расчёт")
    void testDemoVoltageDrop_Success() throws Exception {
        List<Cable> mockCables = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Cable c = mock(Cable.class);
            when(c.getId()).thenReturn((long) i);
            when(c.getConductorCount()).thenReturn(3);
            mockCables.add(c);
        }
        when(cableRepository.findAllWithAllMaterials()).thenReturn(mockCables);

        VoltageDropResponse mockResponse = new VoltageDropResponse(
                "Демо кабель 3x2.5",
                4.20,
                1.05,
                395.80,
                400.0,
                List.of()
        );
        when(voltageDropService.calculate(any(VoltageDropRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/calculation/voltage-drop/demo")
                        .param("cableId", "10")
                        .param("power", "5.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cableName").value("Демо кабель 3x2.5"))
                .andExpect(jsonPath("$.deltaUVolts").value(4.20));
    }
}