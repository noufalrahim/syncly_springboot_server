package com.syncly.syncly.service.impl.base;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.syncly.syncly.filter.FilterCondition;
import com.syncly.syncly.filter.FilterSpecification;
import com.syncly.syncly.service.base.BaseService;
import com.syncly.syncly.wrapper.ServiceResponse;

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
    public ServiceResponse<List<T>> findAll() {
        List<T> result = repository.findAll();
        return buildResponse(result);
    }

    @Override
    public ServiceResponse<Optional<T>> findById(ID id) {
        log.info("Fetching entity with ID: {}", id);
        Optional<T> entity = repository.findById(id);
        return buildResponse(entity);
    }

    @Override
    public ServiceResponse<T> create(T entity) throws IllegalArgumentException, IllegalAccessException {
        mapIdsToEntities(entity);
        T saved = repository.save(entity);
        forceLoadRelations(saved);
        return buildResponse(saved);
    }

    @Override
    public ServiceResponse<List<T>> createMany(List<T> entities) {
        List<T> saved = repository.saveAll(entities);
        return buildResponse(saved);
    }

    @Override
    public ServiceResponse<T> createIfNotExist(T entity, String uniqueField) {
        try {
            Field field = entityClass.getDeclaredField(uniqueField);
            field.setAccessible(true);
            Object value = field.get(entity);

            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + uniqueField + " = :val";
            TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
            query.setParameter("val", value);

            List<T> existing = query.getResultList();
            if (!existing.isEmpty()) {
                return buildErrorResponse(uniqueField + " " + value + " already exists");
            }

            T saved = repository.save(entity);
            return buildResponse(saved);

        } catch (Exception e) {
            return buildErrorResponse("Error checking uniqueness: " + e.getMessage(), e);
        }
    }

    @Override
    public ServiceResponse<T> updateById(ID id, T entity) {
        if (!repository.existsById(id)) {
            return buildErrorResponse("Entity with ID " + id + " not found");
        }
        T saved = repository.save(entity);
        return buildResponse(saved);
    }

    @Override
    public ServiceResponse<?> delete(ID id) {
        if (!repository.existsById(id)) {
            return buildErrorResponse("Entity with ID " + id + " not found");
        }
        repository.deleteById(id);
        return buildResponse("Deleted successfully");
    }

    @Override
    public ServiceResponse<List<T>> findAllByFields(Map<String, Object> filter, String orderBy, String orderDir) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";

        if (filter != null && !filter.isEmpty()) {
            List<String> conditions = filter.keySet().stream().map(key -> {
                if (key.endsWith("Id")) {
                    String relation = key.substring(0, key.length() - 2);
                    return "e." + relation + ".id = :" + key;
                } else {
                    return "e." + key + " = :" + key;
                }
            }).collect(Collectors.toList());

            jpql += " WHERE " + String.join(" AND ", conditions);
        }

        if (orderBy != null) {
            jpql += " ORDER BY e." + orderBy + " " + (orderDir != null && orderDir.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        }

        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);

        if (filter != null && !filter.isEmpty()) {
            filter.forEach(query::setParameter);
        }

        return buildResponse(query.getResultList());
    }

    @Override
    public ServiceResponse<List<T>> findAllByConditions(List<FilterCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return buildResponse(repository.findAll());
        }

        Specification<T> spec = conditions.stream()
                .map(FilterSpecification<T>::new)
                .map(fs -> (Specification<T>) fs)
                .reduce(Specification::and)
                .orElse(null);

        return buildResponse(specRepository.findAll(spec));
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
                        Object relatedEntity = entityManager.find(relationField.getType(), UUID.fromString(idValue.toString()));
                        relationField.set(entity, relatedEntity);
                    } catch (NoSuchFieldException ignored) {}
                }
            }
        }
    }

    private void forceLoadRelations(T entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java")) {
                field.setAccessible(true);
                try {
                    Object relation = field.get(entity);
                    if (relation != null) {
                        relation = entityManager.find(relation.getClass(),
                                entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(relation));
                        field.set(entity, relation);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private <R> ServiceResponse<R> buildResponse(R data) {
        ServiceResponse<R> response = new ServiceResponse<>();
        response.setData(data);
        response.setStatus(ServiceResponse.ResStatus.SUCCESS);
        response.setMessage("Operation successful");
        response.setStatusCode(200);
        return response;
    }

    private <R> ServiceResponse<R> buildErrorResponse(String message) {
        ServiceResponse<R> response = new ServiceResponse<>();
        response.setStatus(ServiceResponse.ResStatus.ERROR);
        response.setMessage(message);
        response.setStatusCode(400);
        return response;
    }

    private <R> ServiceResponse<R> buildErrorResponse(String message, Exception e) {
        ServiceResponse<R> response = new ServiceResponse<>();
        response.setStatus(ServiceResponse.ResStatus.ERROR);
        response.setMessage(message);
        response.setErrorStackTrace(e.toString());
        response.setStatusCode(500);
        return response;
    }
}
