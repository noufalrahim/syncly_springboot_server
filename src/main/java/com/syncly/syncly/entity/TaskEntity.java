package com.syncly.syncly.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue
    private  UUID id;

    private String title;
    private String description;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="column_id")
    private ColumnEntity column;

    private String priority;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "label_id")
    private LabelEntity label;

    private LocalDate dueDate;

    private Instant createdAt;
    private Instant updatedAt;
}
