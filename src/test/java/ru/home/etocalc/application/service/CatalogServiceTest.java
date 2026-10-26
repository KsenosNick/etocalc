package ru.home.etocalc.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CableRepository cableRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    @DisplayName("getAllCables возвращает список кабелей из репозитория")
    void getAllCables_ReturnsListOfCables() {
        // Arrange
        Cable cable1 = mock(Cable.class);
        Cable cable2 = mock(Cable.class);
        when(cableRepository.findAllWithAllMaterials()).thenReturn(List.of(cable1, cable2));

        List<Cable> result = catalogService.getAllCables();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cableRepository, times(1)).findAllWithAllMaterials();
    }

    @Test
    @DisplayName("getAllCables возвращает пустой список, если база данных пуста")
    void getAllCables_ReturnsEmptyList() {
        when(cableRepository.findAllWithAllMaterials()).thenReturn(List.of());

        List<Cable> result = catalogService.getAllCables();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cableRepository, times(1)).findAllWithAllMaterials();
    }
}