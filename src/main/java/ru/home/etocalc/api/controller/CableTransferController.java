package ru.home.etocalc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.home.etocalc.api.dto.export.CableExport;
import ru.home.etocalc.api.dto.export.ExportFile;
import ru.home.etocalc.api.dto.response.ImportResult;
import ru.home.etocalc.application.service.CableExportImportService;
import ru.home.etocalc.domain.enums.ImportMode;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/catalog/cables")
@RequiredArgsConstructor
public class CableTransferController {

    private final CableExportImportService transferService;

    /** Экспорт всего справочника */
    @GetMapping("/export")
    public ResponseEntity<ExportFile<CableExport>> exportAll() {
        return asFile(transferService.exportAll());
    }

    /** Экспорт отмеченных чекбоксами */
    @PostMapping("/export")
    public ResponseEntity<ExportFile<CableExport>> exportSelected(@RequestBody List<Long> ids) {
        return asFile(transferService.exportSelected(ids));
    }

    /** Импорт */
    @PostMapping("/import")
    public ResponseEntity<ImportResult> importCables(
            @RequestBody ExportFile<CableExport> file,
            @RequestParam(defaultValue = "SKIP") ImportMode mode) {
        return ResponseEntity.ok(transferService.importCables(file, mode));
    }

    private ResponseEntity<ExportFile<CableExport>> asFile(ExportFile<CableExport> file) {
        String filename = "cables_%s.json".formatted(LocalDate.now());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(file);
    }
}