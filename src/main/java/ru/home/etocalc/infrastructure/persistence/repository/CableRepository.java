package ru.home.etocalc.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.home.etocalc.domain.model.catalog.Cable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CableRepository extends JpaRepository<Cable, Long>, JpaSpecificationExecutor<Cable> {

    @Query("SELECT c FROM Cable c " +
            "JOIN FETCH c.cableType ct " +
            "JOIN FETCH ct.conductorMaterial " +
            "JOIN FETCH ct.insulationMaterial " +
            "JOIN FETCH ct.sheathMaterial " +
            "WHERE c.id = :id")
    Optional<Cable> findByIdWithMaterials(@Param("id") Long id);

    @Query("SELECT c FROM Cable c " +
            "JOIN FETCH c.cableType ct " +
            "JOIN FETCH ct.conductorMaterial " +
            "JOIN FETCH ct.insulationMaterial " +
            "JOIN FETCH ct.sheathMaterial " +
            "WHERE c.id = :id")
    Optional<Cable> findByIdWithAllMaterials(@Param("id") Long id);

    @Query("SELECT c FROM Cable c " +
            "JOIN FETCH c.cableType ct " +
            "JOIN FETCH ct.conductorMaterial " +
            "JOIN FETCH ct.insulationMaterial " +
            "JOIN FETCH ct.sheathMaterial")
    List<Cable> findAllWithAllMaterials();

    @Query("""
           SELECT c FROM Cable c
           JOIN FETCH c.cableType ct
           WHERE ct.name = :typeName
             AND c.nominalVoltage = :voltage
             AND c.conductorCount = :conductorCount
             AND c.crossSection = :crossSection
           """)
    Optional<Cable> findByNaturalKey(@Param("typeName") String typeName,
                                     @Param("voltage") Integer voltage,
                                     @Param("conductorCount") Integer conductorCount,
                                     @Param("crossSection") BigDecimal crossSection);
}