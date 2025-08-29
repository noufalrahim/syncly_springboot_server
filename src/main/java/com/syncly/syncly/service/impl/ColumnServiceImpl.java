package com.syncly.syncly.service.impl;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.syncly.syncly.entity.ColumnEntity;
import com.syncly.syncly.repository.ColumnRepository;
import com.syncly.syncly.service.ColumnService;
import com.syncly.syncly.service.impl.base.BaseServiceImpl;

@Service
public class ColumnServiceImpl extends BaseServiceImpl<ColumnEntity, UUID> implements ColumnService {
    public ColumnServiceImpl(ColumnRepository columnRepository, JpaSpecificationExecutor<ColumnEntity> specRepository) {
        super(columnRepository, specRepository, ColumnEntity.class);
    }
}
