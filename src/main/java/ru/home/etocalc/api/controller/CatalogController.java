package ru.home.etocalc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.home.etocalc.api.dto.request.CableRequest;
import ru.home.etocalc.api.dto.response.CableResponse;
import ru.home.etocalc.api.dto.response.CableTypeResponse;
import ru.home.etocalc.api.dto.response.PageResponse;
import ru.home.etocalc.application.service.CatalogService;
import ru.home.etocalc.domain.model.catalog.CableType;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/cables")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping
    public PageResponse<CableResponse> getCables(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer conductorCount,
            @RequestParam(required = false) String minCrossSection,
            @RequestParam(required = false) String maxCrossSection,
            @PageableDefault(size = 50, sort = "nominalVoltage") Pageable pageable) {

        return catalogService.getCables(search, conductorCount, minCrossSection, maxCrossSection, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CableResponse> getCableById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCableById(id));
    }

    @PostMapping
    public ResponseEntity<CableResponse> createCable(@RequestBody CableRequest request) {
        return ResponseEntity.ok(catalogService.createCable(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CableResponse> updateCable(@PathVariable Long id, @RequestBody CableRequest request) {
        return ResponseEntity.ok(catalogService.updateCable(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCable(@PathVariable Long id) {
        catalogService.deleteCable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteCables(@RequestBody List<Long> ids) {
        catalogService.deleteCables(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/types")
    public List<CableTypeResponse> getCableTypes() {
        return catalogService.getAllCableTypes();
    }
}