package ru.home.etocalc.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.home.etocalc.api.dto.export.CableExport;
import ru.home.etocalc.api.dto.export.ExportFile;
import ru.home.etocalc.api.dto.response.ImportResult;
import ru.home.etocalc.domain.enums.ImportMode;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.domain.model.catalog.CableType;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;
import ru.home.etocalc.infrastructure.persistence.repository.CableTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CableExportImportService {

    public static final String ENTITY_TYPE = "CABLE";

    private final CableRepository cableRepository;
    private final CableTypeRepository cableTypeRepository;

    // ---------- ЭКСПОРТ ----------

    @Transactional(readOnly = true)
    public ExportFile<CableExport> exportAll() {
        List<CableExport> items = cableRepository.findAllWithAllMaterials().stream()
                .map(this::toExport)
                .toList();
        return ExportFile.of(ENTITY_TYPE, items);
    }

    @Transactional(readOnly = true)
    public ExportFile<CableExport> exportSelected(List<Long> ids) {
        List<CableExport> items = cableRepository.findAllWithAllMaterials().stream()
                .filter(c -> ids.contains(c.getId()))
                .map(this::toExport)
                .toList();
        return ExportFile.of(ENTITY_TYPE, items);
    }

    // ---------- ИМПОРТ ----------

    @Transactional
    public ImportResult importCables(ExportFile<CableExport> file, ImportMode mode) {
        if (!ENTITY_TYPE.equals(file.entityType())) {
            throw new IllegalArgumentException(
                    "Файл содержит данные типа '%s', ожидался '%s'".formatted(file.entityType(), ENTITY_TYPE));
        }
        if (file.formatVersion() > ExportFile.CURRENT_VERSION) {
            throw new IllegalArgumentException(
                    "Версия формата %d новее поддерживаемой (%d). Обнови приложение."
                            .formatted(file.formatVersion(), ExportFile.CURRENT_VERSION));
        }

        int created = 0, updated = 0, skipped = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < file.items().size(); i++) {
            CableExport item = file.items().get(i);
            try {
                Optional<CableType> typeOpt = cableTypeRepository.findByName(item.cableTypeName());
                if (typeOpt.isEmpty()) {
                    errors.add("Строка %d: тип кабеля '%s' не найден в справочнике"
                            .formatted(i + 1, item.cableTypeName()));
                    continue;
                }

                Optional<Cable> existing = cableRepository.findByNaturalKey(
                        item.cableTypeName(), item.nominalVoltage(),
                        item.conductorCount(), item.crossSection());

                if (existing.isPresent() && mode == ImportMode.SKIP) {
                    skipped++;
                    continue;
                }

                Cable cable;
                if (existing.isPresent() && mode == ImportMode.UPDATE) {
                    cable = existing.get();
                    updated++;
                } else {
                    cable = new Cable();
                    created++;
                }

                applyValues(cable, typeOpt.get(), item);
                cableRepository.save(cable);

            } catch (Exception e) {
                errors.add("Строка %d: %s".formatted(i + 1, e.getMessage()));
            }
        }

        return new ImportResult(created, updated, skipped, errors.size(), errors);
    }

    // ---------- МАППИНГ ----------

    private CableExport toExport(Cable c) {
        return new CableExport(
                c.getCableType().getName(),
                c.getNominalVoltage(),
                c.getConductorCount(),
                c.getCrossSection(),
                c.getRPhActive(),
                c.getR0PhActive(),
                c.getXPhReactive(),
                c.getX0Reactive(),
                c.getMaxCurrentEarth(),
                c.getMaxCurrentAir()
        );
    }

    private void applyValues(Cable cable, CableType type, CableExport dto) {
        cable.setCableType(type);
        cable.setNominalVoltage(dto.nominalVoltage());
        cable.setConductorCount(dto.conductorCount());
        cable.setCrossSection(dto.crossSection());
        cable.setRPhActive(dto.rPhActive());
        cable.setR0PhActive(dto.r0PhActive());
        cable.setXPhReactive(dto.xPhReactive());
        cable.setX0Reactive(dto.x0Reactive());
        cable.setMaxCurrentEarth(dto.maxCurrentEarth());
        cable.setMaxCurrentAir(dto.maxCurrentAir());
    }
}