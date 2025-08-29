package com.syncly.syncly.service.impl;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.syncly.syncly.entity.Project;
import com.syncly.syncly.repository.ProjectRepository;
import com.syncly.syncly.service.ProjectService;
import com.syncly.syncly.service.impl.base.BaseServiceImpl;

@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project, UUID> implements ProjectService {
    public ProjectServiceImpl(ProjectRepository repository, JpaSpecificationExecutor<Project> specRepository){
        super(repository, specRepository, Project.class);
    }
}
