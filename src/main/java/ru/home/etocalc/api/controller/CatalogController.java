package ru.home.etocalc.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.home.etocalc.application.service.CatalogService;
import ru.home.etocalc.domain.model.catalog.Cable;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/cables")
    public List<Cable> getCables() {
        return catalogService.getAllCables();
    }
}