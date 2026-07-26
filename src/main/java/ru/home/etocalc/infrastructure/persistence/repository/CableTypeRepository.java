package ru.home.etocalc.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.home.etocalc.domain.model.catalog.CableType;

public interface CableTypeRepository extends JpaRepository<CableType, Long> {
}