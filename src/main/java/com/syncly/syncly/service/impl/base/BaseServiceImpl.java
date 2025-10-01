package com.syncly.syncly.service.impl.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.syncly.syncly.filter.FilterCondition;
import com.syncly.syncly.filter.FilterSpecification;
import com.syncly.syncly.service.base.BaseService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final JpaSpecificationExecutor<T> specRepository;

    @PersistenceContext
    protected EntityManager entityManager;
    private final Class<T> entityClass;

    public BaseServiceImpl(JpaRepository<T, ID> repository,
            JpaSpecificationExecutor<T> specRepository,
            Class<T> entityClass) {
        this.repository = repository;
        this.specRepository = specRepository;
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T create(T entity) throws IllegalArgumentException, IllegalAccessException {
        mapIdsToEntities(entity);
        T saved = repository.save(entity);

        // Force loading of ManyToOne fields
        for (Field field : saved.getClass().getDeclaredFields()) {
            if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java")) {
                field.setAccessible(true);
                try {
                    Object relation = field.get(saved);
                    if (relation != null) {
                        relation = entityManager.find(relation.getClass(),
                                entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(relation));
                        field.set(saved, relation);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return saved;
    }

    @Override
    public List<T> createMany(List<T> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public T createIfNotExist(T entity, String uniqueField) {
        try {
            Field field = entityClass.getDeclaredField(uniqueField);
            field.setAccessible(true);
            Object value = field.get(entity);

            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + uniqueField + " = :val";
            TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
            query.setParameter("val", value);

            List<T> existing = query.getResultList();
            if (!existing.isEmpty()) {
                throw new RuntimeException(uniqueField + " " + value + " already exists");
            }
            return repository.save(entity);

        } catch (Exception e) {
            throw new RuntimeException("Error checking uniqueness", e);
        }
    }

    @Override
    public T updateById(ID id, T entity) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity with ID " + id + " not found");
        }
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    public List<T> findAllByFields(Map<String, Object> filter, String orderBy, String orderDir) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";

        if (filter != null && !filter.isEmpty()) {
            jpql += " WHERE ";
            List<String> conditions = new ArrayList<>();

            for (String key : filter.keySet()) {
                if (key.endsWith("Id")) {
                    String relation = key.substring(0, key.length() - 2); // remove "Id"
                    conditions.add("e." + relation + ".id = :" + key);
                } else {
                    conditions.add("e." + key + " = :" + key);
                }
            }

            jpql += String.join(" AND ", conditions);
        }

        if (orderBy != null) {
            jpql += " ORDER BY e." + orderBy + " "
                    + (orderDir != null && orderDir.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        }

        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);

        if (filter != null && !filter.isEmpty()) {
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return query.getResultList();
    }

    @Override
    public List<T> findAllByConditions(List<FilterCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return repository.findAll();
        }

        Specification<T> spec = conditions.stream()
                .map(condition -> (Specification<T>) new FilterSpecification<T>(condition))
                .reduce(Specification::and)
                .orElse(null);

        return specRepository.findAll(spec);
    }

    private void mapIdsToEntities(T entity) throws IllegalArgumentException, IllegalAccessException {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.getName().endsWith("Id")) {
                String relationName = field.getName().substring(0, field.getName().length() - 2);
                field.setAccessible(true);
                Object idValue = field.get(entity);
                if (idValue != null) {
                    try {
                        Field relationField = entity.getClass().getDeclaredField(relationName);
                        relationField.setAccessible(true);
                        Class<?> relationClass = relationField.getType();

                        Object relatedEntity = entityManager.find(relationClass, UUID.fromString(idValue.toString()));
                        relationField.set(entity, relatedEntity);

                    } catch (NoSuchFieldException ignored) {
                    }
                }
            }
        }
    }

}
