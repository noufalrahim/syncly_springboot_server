package com.syncly.syncly.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.syncly.syncly.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDTO {
    private UUID id;
    private String name;
    private User createdBy;           
    private List<User> users;         
    private Instant createdAt;
    private Instant updatedAt;
}

// EDIT THIS. CHANGE THE TYPES TO MINIMISE THE DATA. FOR EXAMPLE CHANGE THE TYPE OS USER TO UUID TO GET ONLY USERIDS. OR YOU CAN EVEN COMPLETELY REMOVE UNWANTED FIELDS.