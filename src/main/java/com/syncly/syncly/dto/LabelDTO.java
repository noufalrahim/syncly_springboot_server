package com.syncly.syncly.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelDTO {
    private UUID id;
    private String title;
    private UUID projectId;
    private Instant createdAt;
    private Instant updatedAt;
}
