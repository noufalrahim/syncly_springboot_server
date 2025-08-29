package com.syncly.syncly.service.impl;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.syncly.syncly.entity.Organisation;
import com.syncly.syncly.repository.OrganisationRepository;
import com.syncly.syncly.service.OrganisationService;
import com.syncly.syncly.service.impl.base.BaseServiceImpl;

@Service
public class OrganisationServiceImpl extends BaseServiceImpl<Organisation, UUID> implements OrganisationService{
    public OrganisationServiceImpl(OrganisationRepository repository, JpaSpecificationExecutor<Organisation> specRepository){
        super(repository, specRepository, Organisation.class);
    }
}
    