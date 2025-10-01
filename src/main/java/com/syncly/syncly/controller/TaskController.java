package com.syncly.syncly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.controller.base.BaseController;
import com.syncly.syncly.dto.TaskDTO;
import com.syncly.syncly.entity.TaskEntity;
import com.syncly.syncly.mapper.TaskMapper;
import com.syncly.syncly.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController extends BaseController<TaskEntity, TaskDTO, UUID> {
    public TaskController(TaskService taskService) {
        super(taskService, TaskMapper::toDTO);
    }
}
