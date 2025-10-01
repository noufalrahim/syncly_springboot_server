package com.syncly.syncly.controller.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import com.syncly.syncly.wrapper.ServiceResponse;

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
    public ResponseEntity<ServiceResponse<List<D>>> getAll() {
        ServiceResponse<List<T>> resp = service.findAll();
        if (resp.getStatus() != ServiceResponse.ResStatus.SUCCESS) return ResponseEntity.status(resp.getStatusCode()).body(errorResponse(resp));

        List<D> data = entityToDTO.isPresent() ?
                resp.getData().stream().map(entityToDTO.get()).toList() :
                (List<D>) resp.getData();

        return ResponseEntity.ok(buildResponse(resp, data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<D>> getById(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        ServiceResponse<Optional<T>> resp = service.findById((ID) uuid);

        if (resp.getStatus() != ServiceResponse.ResStatus.SUCCESS) return ResponseEntity.status(resp.getStatusCode()).body(errorResponse(resp));
        if (resp.getData().isEmpty()) return ResponseEntity.ok(buildResponse(resp, null));

        T entity = resp.getData().get();
        D body = entityToDTO.map(dtoFn -> dtoFn.apply(entity)).orElse((D) entity);

        return ResponseEntity.ok(buildResponse(resp, body));
    }

    @PostMapping
    public ResponseEntity<ServiceResponse<D>> create(@RequestBody T entity) throws IllegalArgumentException, IllegalAccessException {
        ServiceResponse<T> resp = service.create(entity);
        if (resp.getStatus() != ServiceResponse.ResStatus.SUCCESS) return ResponseEntity.status(resp.getStatusCode()).body(errorResponse(resp));

        T created = resp.getData();
        D body = entityToDTO.map(dtoFn -> dtoFn.apply(created)).orElse((D) created);
        return ResponseEntity.status(201).body(buildResponse(resp, body));
    }

    @PostMapping("/many")
    public ResponseEntity<ServiceResponse<List<D>>> createMany(@RequestBody List<T> entities) {
        ServiceResponse<List<T>> resp = service.createMany(entities);
        if (resp.getStatus() != ServiceResponse.ResStatus.SUCCESS) return ResponseEntity.status(resp.getStatusCode()).body(errorResponse(resp));

        List<D> data = entityToDTO.isPresent() ? resp.getData().stream().map(entityToDTO.get()).toList() : (List<D>) resp.getData();
        return ResponseEntity.status(201).body(buildResponse(resp, data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse<D>> update(@PathVariable ID id, @RequestBody T entity) {
        ServiceResponse<T> resp = service.updateById(id, entity);
        if (resp.getStatus() != ServiceResponse.ResStatus.SUCCESS) return ResponseEntity.status(resp.getStatusCode()).body(errorResponse(resp));

        D body = entityToDTO.map(dtoFn -> dtoFn.apply(resp.getData())).orElse((D) resp.getData());
        return ResponseEntity.ok(buildResponse(resp, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse<Void>> delete(@PathVariable ID id) {
        ServiceResponse<?> resp = service.delete(id);
        ServiceResponse<Void> response = new ServiceResponse<>();
        response.setStatus(resp.getStatus());
        response.setMessage(resp.getMessage());
        response.setErrorStackTrace(resp.getErrorStackTrace());
        response.setStatusCode(resp.getStatusCode());
        return ResponseEntity.status(resp.getStatus() == ServiceResponse.ResStatus.SUCCESS ? 200 : 500).body(response);
    }

    @GetMapping("/fields/many")
    public ResponseEntity<ServiceResponse<List<D>>> getByFields(@RequestParam Map<String, String> params) {
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

        ServiceResponse<List<T>> resp = service.findAllByConditions(conditions);
        if (resp.getStatus() != ServiceResponse.ResStatus.SUCCESS) return ResponseEntity.status(resp.getStatusCode()).body(errorResponse(resp));

        List<D> data = entityToDTO.isPresent() ? resp.getData().stream().map(entityToDTO.get()).toList() : (List<D>) resp.getData();
        return ResponseEntity.ok(buildResponse(resp, data));
    }

    private <R> ServiceResponse<R> buildResponse(ServiceResponse<?> source, R data) {
        ServiceResponse<R> response = new ServiceResponse<>();
        response.setStatus(source.getStatus());
        response.setMessage(source.getMessage());
        response.setErrorStackTrace(source.getErrorStackTrace());
        response.setStatusCode(source.getStatusCode());
        response.setData(data);
        return response;
    }

    private <R> ServiceResponse<R> errorResponse(ServiceResponse<?> source) {
        return buildResponse(source, null);
    }
}
