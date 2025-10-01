package com.syncly.syncly.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID projectId;
    private UUID columnId;
    private String priority;
    private UUID labelId;
    private LocalDate dueDate;
    private Instant createdAt;
    private Instant updatedAt;
}
