package ru.home.etocalc.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.home.etocalc.api.dto.request.CableRequest;
import ru.home.etocalc.api.dto.response.CableResponse;
import ru.home.etocalc.api.dto.response.CableTypeResponse;
import ru.home.etocalc.api.dto.response.PageResponse;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.domain.model.catalog.CableType;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;
import ru.home.etocalc.infrastructure.persistence.repository.CableTypeRepository;
import ru.home.etocalc.infrastructure.persistence.specification.CableSpecification;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CableRepository cableRepository;
    private final CableTypeRepository cableTypeRepository;

    // 1. Чтение с фильтрацией и сортировкой
    @Transactional(readOnly = true) // <-- КРИТИЧЕСКИ ВАЖНО: держит сессию открытой для маппинга
    public PageResponse<CableResponse> getCables(String cableTypeName, Integer conductorCount,
                                                 String minCrossSection, String maxCrossSection, Pageable pageable) {

        Specification<Cable> spec = Specification.where(CableSpecification.hasCableTypeName(cableTypeName))
                .and(CableSpecification.hasConductorCount(conductorCount))
                .and(CableSpecification.hasCrossSectionBetween(
                        minCrossSection != null ? new BigDecimal(minCrossSection) : null,
                        maxCrossSection != null ? new BigDecimal(maxCrossSection) : null
                ));

        Page<Cable> page = cableRepository.findAll(spec, pageable);

        // Маппинг происходит ЗДЕСЬ, внутри @Transactional метода.
        // Даже если данные ленивые, они загрузятся без ошибок.
        return PageResponse.from(page, CableResponse::from);
    }

    // 2. Чтение одного элемента
    @Transactional(readOnly = true)
    public CableResponse getCableById(Long id) {
        Cable cable = cableRepository.findByIdWithAllMaterials(id)
                .orElseThrow(() -> new RuntimeException("Кабель с ID " + id + " не найден"));
        return CableResponse.from(cable);
    }

    // 3. Создание
    @Transactional
    public CableResponse createCable(CableRequest request) {
        validateCableTypeExists(request.cableTypeId());
        Cable cable = new Cable();
        mapRequestToEntity(request, cable);
        return CableResponse.from(cableRepository.save(cable));
    }

    // 4. Обновление
    @Transactional
    public CableResponse updateCable(Long id, CableRequest request) {
        Cable cable = getCableByIdEntity(id);
        validateCableTypeExists(request.cableTypeId());
        mapRequestToEntity(request, cable);
        return CableResponse.from(cableRepository.save(cable));
    }

    // 5. Удаление одного
    @Transactional
    public void deleteCable(Long id) {
        if (!cableRepository.existsById(id)) {
            throw new RuntimeException("Кабель с ID " + id + " не найден");
        }
        cableRepository.deleteById(id);
    }

    // 6. Массовое удаление
    @Transactional
    public void deleteCables(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        cableRepository.deleteAllById(ids);
    }

    // 7. Получение всех типов кабелей
    @Transactional(readOnly = true)
    public List<CableTypeResponse> getAllCableTypes() {
        // Загружаем всё одним запросом благодаря JOIN FETCH
        List<CableType> types = cableTypeRepository.findAllWithMaterials();

        // Маппим в плоские DTO
        return types.stream()
                .map(CableTypeResponse::from)
                .toList();
    }



    // --- Вспомогательные методы ---

    private void validateCableTypeExists(Long cableTypeId) {
        if (!cableTypeRepository.existsById(cableTypeId)) {
            throw new RuntimeException("Тип кабеля с ID " + cableTypeId + " не найден");
        }
    }

    private Cable getCableByIdEntity(Long id) {
        return cableRepository.findByIdWithAllMaterials(id)
                .orElseThrow(() -> new RuntimeException("Кабель с ID " + id + " не найден"));
    }

    private void mapRequestToEntity(CableRequest request, Cable cable) {
        CableType type = new CableType();
        type.setId(request.cableTypeId());

        cable.setCableType(type);
        cable.setNominalVoltage(request.nominalVoltage());
        cable.setConductorCount(request.conductorCount());
        cable.setCrossSection(request.crossSection());
        cable.setRPhActive(request.rPhActive());
        cable.setR0PhActive(request.r0PhActive());
        cable.setXPhReactive(request.xPhReactive());
        cable.setX0Reactive(request.x0Reactive());
        cable.setMaxCurrentEarth(request.maxCurrentEarth());
        cable.setMaxCurrentAir(request.maxCurrentAir());
    }
}