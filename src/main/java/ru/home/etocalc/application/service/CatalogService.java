package ru.home.etocalc.application.service;

import jakarta.persistence.criteria.Expression;
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
    @Transactional(readOnly = true)
    public PageResponse<CableResponse> getCables(String search, Integer conductorCount,
                                                 String minCrossSection, String maxCrossSection,
                                                 Pageable pageable) {
        Specification<Cable> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            String normalized = normalizeSearch(search);
            spec = spec.and((root, query, cb) -> {
                Expression<String> typeName = cb.lower(root.get("cableType").get("name"));
                Expression<String> cores = root.get("conductorCount").as(String.class);
                Expression<String> section = root.get("crossSection").as(String.class);

                // Собираем строку вида "ввгнг(а)-ls 3×1.50"
                Expression<String> full = cb.concat(
                        cb.concat(typeName, " "),
                        cb.concat(cores, cb.concat("×", section))
                );
                // Убираем пробелы, чтобы "ввг3х1.5" тоже находило
                Expression<String> noSpaces = cb.function(
                        "replace", String.class, full, cb.literal(" "), cb.literal(""));

                return cb.like(noSpaces, "%" + normalized + "%");
            });
        }

        if (conductorCount != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("conductorCount"), conductorCount));
        }
        if (minCrossSection != null && !minCrossSection.isBlank()) {
            BigDecimal min = new BigDecimal(minCrossSection);
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("crossSection"), min));
        }
        if (maxCrossSection != null && !maxCrossSection.isBlank()) {
            BigDecimal max = new BigDecimal(maxCrossSection);
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("crossSection"), max));
        }

        Page<Cable> page = cableRepository.findAll(spec, pageable);
        return PageResponse.from(page, CableResponse::from);
    }

    /** Приводит запрос к единому виду: х/x/× → ×, без пробелов, нижний регистр */
    private String normalizeSearch(String raw) {
        return raw.trim()
                .replace('х', '×')  // кириллическая х
                .replace('x', '×')  // латинская x
                .replace(" ", "")
                .toLowerCase();
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