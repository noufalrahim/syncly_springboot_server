package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.ColumnDTO;
import com.syncly.syncly.entity.ColumnEntity;

public class ColumnMapper {
    public static ColumnDTO toDTO(ColumnEntity column) {
        if (column == null) return null;
        return new ColumnDTO(
            column.getId(),
            column.getName(),
            column.getProject() != null ? column.getProject().getId() : null,
            column.getCreatedAt(),
            column.getUpdatedAt()
        );
    }
}

