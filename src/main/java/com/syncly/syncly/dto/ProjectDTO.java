package com.syncly.syncly.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private UUID id;
    private String name;
    private String emoji;
    private OrganisationDTO organisation;
    private Instant createdAt;
    private Instant updatedAt;
}
