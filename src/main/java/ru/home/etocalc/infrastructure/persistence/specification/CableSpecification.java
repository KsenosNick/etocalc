package ru.home.etocalc.infrastructure.persistence.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.home.etocalc.domain.model.catalog.Cable;

import java.math.BigDecimal;

public class CableSpecification {

    public static Specification<Cable> hasCableTypeName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("cableType").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Cable> hasConductorCount(Integer count) {
        return (root, query, cb) -> count == null
                ? cb.conjunction()
                : cb.equal(root.get("conductorCount"), count);
    }

    public static Specification<Cable> hasCrossSectionBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("crossSection"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("crossSection"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("crossSection"), max);
            }
            return cb.conjunction();
        };
    }
}