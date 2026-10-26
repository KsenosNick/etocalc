package ru.home.etocalc.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.home.etocalc.application.service.CatalogService;
import ru.home.etocalc.domain.model.catalog.Cable;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CatalogService catalogService;

    @Test
    @DisplayName("GET /api/catalog/cables возвращает список кабелей с кодом 200 OK")
    void getCables_ReturnsOkAndListOfCables() throws Exception {
        Cable mockCable = mock(Cable.class);
        when(mockCable.getId()).thenReturn(1L);
        when(mockCable.getFullName()).thenReturn("ВВГнг(А)-LS 3x2.5");

        when(catalogService.getAllCables()).thenReturn(List.of(mockCable));

        // Act & Assert
        mockMvc.perform(get("/api/catalog/cables")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("ВВГнг(А)-LS 3x2.5"));
    }

    @Test
    @DisplayName("GET /api/catalog/cables возвращает пустой массив, если каталог пуст")
    void getCables_ReturnsEmptyArray() throws Exception {
        // Arrange
        when(catalogService.getAllCables()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/catalog/cables")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}