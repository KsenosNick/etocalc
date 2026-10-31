package ru.home.etocalc.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.home.etocalc.domain.model.catalog.CableType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CableTypeRepository extends JpaRepository<CableType, Long> {

    /** Загрузка всех типов с материалами одним запросом (для выпадающих списков) */
    @Query("SELECT ct FROM CableType ct " +
            "JOIN FETCH ct.conductorMaterial " +
            "JOIN FETCH ct.insulationMaterial " +
            "JOIN FETCH ct.sheathMaterial")
    List<CableType> findAllWithMaterials();

    /** Поиск типа по имени (для импорта кабелей) */
    Optional<CableType> findByName(String name);
}