package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.OrganisationDTO;
import com.syncly.syncly.dto.ProjectDTO;
import com.syncly.syncly.entity.Project;

public class ProjectMapper {
    public static ProjectDTO toDTO(Project project) {
        if (project == null) return null;

        OrganisationDTO orgDTO = null;
        if (project.getOrganisation() != null) {
            orgDTO = new OrganisationDTO(
                project.getOrganisation().getId(),
                project.getOrganisation().getName(),
                project.getOrganisation().getCreatedBy() != null 
                    ? project.getOrganisation().getCreatedBy()
                    : null,
                project.getOrganisation().getUsers() != null 
                    ? project.getOrganisation().getUsers()
                    : null,
                project.getOrganisation().getCreatedAt(),
                project.getOrganisation().getUpdatedAt()
            );
        }

        return new ProjectDTO(
            project.getId(),
            project.getName(),
            project.getEmoji(),
            orgDTO,
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }
}
