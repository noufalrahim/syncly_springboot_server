package com.syncly.syncly.filter;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FilterSpecification<T> implements Specification<T> {

    private final FilterCondition condition;

    public FilterSpecification(FilterCondition condition) {
        this.condition = condition;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Path<?> path = root.get(condition.getField());
        Object[] values = condition.getValues();

        return switch (condition.getOperator()) {
            case EQ -> cb.equal(path, values[0]);
            case NE -> cb.notEqual(path, values[0]);
            case GT -> cb.greaterThan(path.as(Comparable.class), (Comparable) values[0]);
            case GTE -> cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) values[0]);
            case LT -> cb.lessThan(path.as(Comparable.class), (Comparable) values[0]);
            case LTE -> cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) values[0]);
            case LIKE -> cb.like(cb.lower(path.as(String.class)), "%" + values[0].toString().toLowerCase() + "%");
            case BETWEEN -> cb.between(path.as(Comparable.class), (Comparable) values[0], (Comparable) values[1]);
        };
    }
}