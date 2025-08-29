package com.syncly.syncly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.controller.base.BaseController;
import com.syncly.syncly.dto.ProjectDTO;
import com.syncly.syncly.entity.Project;
import com.syncly.syncly.mapper.ProjectMapper;
import com.syncly.syncly.service.ProjectService;

@RestController
@RequestMapping("/projects")
public class ProjectController extends BaseController<Project, ProjectDTO, UUID> {

    public ProjectController(ProjectService service) {
        super(service, ProjectMapper::toDTO);
    }
}
