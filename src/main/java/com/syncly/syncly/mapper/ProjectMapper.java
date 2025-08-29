package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.ProjectDTO;
import com.syncly.syncly.entity.Project;

public class ProjectMapper {
    public static ProjectDTO toDTO(Project project) {
        if (project == null) return null;
        return new ProjectDTO(
            project.getId(),
            project.getName(),
            project.getEmoji(),
            project.getOrganisation() != null ? project.getOrganisation().getId() : null,
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}
