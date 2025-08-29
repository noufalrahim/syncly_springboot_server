package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.LabelDTO;
import com.syncly.syncly.entity.LabelEntity;


public class LabelMapper {
    public static LabelDTO toDTO(LabelEntity label) {
        if (label == null) return null;
        return new LabelDTO(
            label.getId(),
            label.getTitle(),
            label.getProject() != null ? label.getProject().getId() : null,
            label.getCreatedAt(),
            label.getUpdatedAt()
        );
    }
};

