package com.syncly.syncly.service.base;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.syncly.syncly.filter.FilterCondition;
import com.syncly.syncly.wrapper.ServiceResponse;

public interface BaseService<T, ID> {
    ServiceResponse<List<T>> findAll();
    ServiceResponse<Optional<T>> findById(ID id);
    ServiceResponse<T> create(T entity) throws IllegalArgumentException, IllegalAccessException;
    ServiceResponse<List<T>> createMany(List<T> entities);
    ServiceResponse<T> createIfNotExist(T entity, String uniqueField);
    ServiceResponse<T> updateById(ID id, T entity);
    ServiceResponse<?> delete(ID id);
    ServiceResponse<List<T>> findAllByFields(Map<String, Object> filter, String orderBy, String orderDir);
    ServiceResponse<List<T>> findAllByConditions(List<FilterCondition> conditions);
}
