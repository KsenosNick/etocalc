package ru.home.etocalc.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.home.etocalc.domain.model.catalog.Cable;

import java.util.List;

@Repository
public interface CableRepository extends JpaRepository<Cable, Long> {

    @Query("SELECT c FROM Cable c " +
            "JOIN FETCH c.cableType ct " +
            "JOIN FETCH ct.conductorMaterial " +
            "JOIN FETCH ct.insulationMaterial " +
            "JOIN FETCH ct.sheathMaterial")
    List<Cable> findAllWithAllMaterials();
}