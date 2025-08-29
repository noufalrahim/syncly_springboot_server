package com.syncly.syncly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.controller.base.BaseController;
import com.syncly.syncly.entity.Organisation;
import com.syncly.syncly.service.OrganisationService;


@RestController
@RequestMapping("/organisations")
public class OrganisationController extends BaseController<Organisation, Object, UUID> {
    public OrganisationController(OrganisationService service) {
        super(service);
    }
}
