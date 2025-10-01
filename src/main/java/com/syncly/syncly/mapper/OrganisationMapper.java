package com.syncly.syncly.mapper;

import com.syncly.syncly.dto.OrganisationDTO;
import com.syncly.syncly.entity.Organisation;

public class OrganisationMapper {
    public static OrganisationDTO toDTO(Organisation organisation) {
        if (organisation == null) return null;

        return new OrganisationDTO(
            organisation.getId(),
            organisation.getName(),
            organisation.getCreatedBy() != null ? organisation.getCreatedBy(): null,
            organisation.getUsers() != null ? 
                organisation.getUsers() : null,
            organisation.getCreatedAt(),
            organisation.getUpdatedAt()
        );
    }
}
