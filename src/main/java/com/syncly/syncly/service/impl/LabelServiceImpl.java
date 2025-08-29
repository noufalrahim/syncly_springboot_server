package com.syncly.syncly.service.impl;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.syncly.syncly.entity.LabelEntity;
import com.syncly.syncly.repository.LabelRepository;
import com.syncly.syncly.service.LabelService;
import com.syncly.syncly.service.impl.base.BaseServiceImpl;

@Service
public class LabelServiceImpl extends BaseServiceImpl<LabelEntity, UUID> implements LabelService {
    public LabelServiceImpl(LabelRepository labelRepository, JpaSpecificationExecutor<LabelEntity> specRepository) {
        super(labelRepository, specRepository, LabelEntity.class);
    }
}
