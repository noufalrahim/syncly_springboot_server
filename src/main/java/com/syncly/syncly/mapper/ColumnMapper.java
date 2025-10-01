package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.ColumnDTO;
import com.syncly.syncly.dto.ProjectDTO;
import com.syncly.syncly.entity.ColumnEntity;

public class ColumnMapper {
    public static ColumnDTO toDTO(ColumnEntity column) {
        if (column == null) return null;

        ProjectDTO prDto = null;
        if (column.getProject() != null) {
            prDto = ProjectMapper.toDTO(column.getProject());
        }

        return new ColumnDTO(
            column.getId(),
            column.getName(),
            prDto,
            column.getCreatedAt(),
            column.getUpdatedAt()
        );
    }
}
