package com.syncly.syncly.service.impl.base;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

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

@Transactional
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
    public T create(T entity) {
        return repository.save(entity);
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
    public List<T> findAllByFields(Object filter, String orderBy, String orderDir) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        if (orderBy != null) {
            jpql += " ORDER BY e." + orderBy + " " + (orderDir.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        }
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    @Override
    public List<T> findAllByConditions(List<FilterCondition> conditions) {
        Specification<T> spec = null;

        for (FilterCondition condition : conditions) {
            Specification<T> conditionSpec = new FilterSpecification<>(condition);
            spec = (spec == null) ? conditionSpec : spec.and(conditionSpec);
        }

        if (spec == null) {
            return repository.findAll();
        }
        return specRepository.findAll(spec);
    }

}
