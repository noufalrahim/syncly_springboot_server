package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.TaskDTO;
import com.syncly.syncly.entity.TaskEntity;

public class TaskMapper {
    public static TaskDTO toDTO(TaskEntity task) {
        if (task == null) return null;
        return new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getProject() != null ? task.getProject().getId() : null,
            task.getColumn() != null ? task.getColumn().getId() : null,
            task.getPriority(),
            task.getLabel() != null ? task.getLabel().getId() : null,
            task.getDueDate(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}
