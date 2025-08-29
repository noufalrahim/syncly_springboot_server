package com.syncly.syncly.service.base;

import java.util.List;
import java.util.Optional;

import com.syncly.syncly.filter.FilterCondition;

public interface BaseService<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T create(T entity);
    List<T> createMany(List<T> entities);
    T createIfNotExist(T entity, String uniqueField);
    T updateById(ID id, T entity);
    void delete(ID id);
    List<T> findAllByFields(Object filter, String orderBy, String orderDir);
    List<T> findAllByConditions(List<FilterCondition> conditions);
}
