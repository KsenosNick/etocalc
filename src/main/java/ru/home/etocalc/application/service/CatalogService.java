package ru.home.etocalc.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.home.etocalc.domain.model.catalog.Cable;
import ru.home.etocalc.infrastructure.persistence.repository.CableRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CableRepository cableRepository;

    public List<Cable> getAllCables() {
        return cableRepository.findAllWithAllMaterials();
    }
}