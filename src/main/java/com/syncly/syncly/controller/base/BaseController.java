package com.syncly.syncly.controller.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.syncly.syncly.filter.FilterCondition;
import com.syncly.syncly.filter.FilterOperator;
import com.syncly.syncly.service.base.BaseService;

public abstract class BaseController<T, D, ID> {

    protected final BaseService<T, ID> service;
    protected final Optional<Function<T, D>> entityToDTO;

    protected BaseController(BaseService<T, ID> service) {
        this.service = service;
        this.entityToDTO = Optional.empty();
    }

    protected BaseController(BaseService<T, ID> service, Function<T, D> entityToDTO) {
        this.service = service;
        this.entityToDTO = Optional.of(entityToDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<T> entities = service.findAll();
        if (entityToDTO.isPresent()) {
            List<D> dtos = entities.stream()
                    .map(entityToDTO.get())
                    .toList();
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable ID id) {
        Optional<T> entityOpt = service.findById(id);
        if (entityOpt.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        T entity = entityOpt.get();
        Object body;
        if (entityToDTO.isPresent()) {
            body = entityToDTO.get().apply(entity);
        } else {
            body = entity;
        }

        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody T entity) throws IllegalArgumentException, IllegalAccessException {
        T created = service.create(entity);

        // Force full loading of all ManyToOne relations ending with "Id"
        for (Field field : created.getClass().getDeclaredFields()) {
            if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java")) {
                field.setAccessible(true);
                try {
                    Object value = field.get(created);
                    if (value != null) {
                        // Trigger lazy load by accessing any field
                        for (Field subField : value.getClass().getDeclaredFields()) {
                            subField.setAccessible(true);
                            subField.get(value);
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException ignored) {
                }
            }
        }

        Object body;
        if (entityToDTO.isPresent()) {
            body = entityToDTO.get().apply(created);
        } else {
            body = created;
        }
        return ResponseEntity.status(201).body(body);
    }

    @PostMapping("/many")
    public ResponseEntity<?> createMany(@RequestBody List<T> entities) {
        List<T> created = service.createMany(entities);
        if (entityToDTO.isPresent()) {
            List<D> dtos = created.stream().map(entityToDTO.get()).toList();
            return ResponseEntity.status(201).body(dtos);
        }
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody T entity) {
        T updated = service.updateById(id, entity);
        Object body; // can be DTO or entity
        if (entityToDTO.isPresent()) {
            body = entityToDTO.get().apply(updated);
        } else {
            body = updated;
        }
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fields/many")
    public ResponseEntity<?> getByFields(@RequestParam Map<String, String> params) {
        List<FilterCondition> conditions = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String field = key;
            FilterOperator operator = FilterOperator.EQ;

            if (key.contains("[")) {
                field = key.substring(0, key.indexOf("["));
                String op = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                operator = FilterOperator.valueOf(op.toUpperCase());
            }

            if (operator == FilterOperator.BETWEEN) {
                String[] parts = value.split(",");
                conditions.add(new FilterCondition(field, operator, parts));
            } else {
                conditions.add(new FilterCondition(field, operator, new Object[]{value}));
            }
        }

        List<T> entities = service.findAllByConditions(conditions);
        if (entityToDTO.isPresent()) {
            List<D> dtos = entities.stream().map(entityToDTO.get()).toList();
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.ok(entities);
    }
}
