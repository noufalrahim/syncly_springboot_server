package com.syncly.syncly.filter;

import java.util.UUID;

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
        Path<?> path = buildPath(root, condition.getField());
        Object[] values = convertValues(path, condition.getValues());

        switch (condition.getOperator()) {
            case EQ:
                return cb.equal(path, values[0]);
            case NE:
                return cb.notEqual(path, values[0]);
            case GT:
                return cb.greaterThan(path.as(Comparable.class), (Comparable) values[0]);
            case GTE:
                return cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) values[0]);
            case LT:
                return cb.lessThan(path.as(Comparable.class), (Comparable) values[0]);
            case LTE:
                return cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) values[0]);
            case LIKE:
                return cb.like(cb.lower(path.as(String.class)), "%" + values[0].toString().toLowerCase() + "%");
            case BETWEEN:
                return cb.between(path.as(Comparable.class), (Comparable) values[0], (Comparable) values[1]);
            default:
                throw new IllegalArgumentException("Unsupported operator: " + condition.getOperator());
        }
    }

    private Path<?> buildPath(Path<?> root, String field) {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }

    private Object[] convertValues(Path<?> path, Object[] values) {
        Object[] converted = new Object[values.length];
        Class<?> type = path.getJavaType();

        for (int i = 0; i < values.length; i++) {
            if (type.equals(UUID.class) && values[i] instanceof String) {
                converted[i] = UUID.fromString((String) values[i]);
            } else if (type.equals(Integer.class) && values[i] instanceof String) {
                converted[i] = Integer.parseInt((String) values[i]);
            } else if (type.equals(Long.class) && values[i] instanceof String) {
                converted[i] = Long.parseLong((String) values[i]);
            } else if (type.equals(Boolean.class) && values[i] instanceof String) {
                converted[i] = Boolean.parseBoolean((String) values[i]);
            } else {
                converted[i] = values[i];
            }
        }

        return converted;
    }
}
