package com.syncly.syncly.service.impl;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.syncly.syncly.entity.TaskEntity;
import com.syncly.syncly.repository.TaskRepository;
import com.syncly.syncly.service.TaskService;
import com.syncly.syncly.service.impl.base.BaseServiceImpl;

@Service
public class TaskServiceImpl extends BaseServiceImpl<TaskEntity, UUID> implements TaskService{
    public TaskServiceImpl(TaskRepository taskRepository, JpaSpecificationExecutor<TaskEntity> specRepository) {
        super(taskRepository, specRepository, TaskEntity.class);
    }
}
