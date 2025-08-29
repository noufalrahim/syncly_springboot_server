package com.syncly.syncly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.controller.base.BaseController;
import com.syncly.syncly.dto.LabelDTO;
import com.syncly.syncly.entity.LabelEntity;
import com.syncly.syncly.mapper.LabelMapper;
import com.syncly.syncly.service.LabelService;

@RestController
@RequestMapping("/labels")
public class LabelController extends BaseController<LabelEntity, LabelDTO, UUID> {

    public LabelController(LabelService service) {
        super(service, LabelMapper::toDTO);
    }
}
