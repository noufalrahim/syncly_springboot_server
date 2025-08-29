package com.syncly.syncly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.controller.base.BaseController;
import com.syncly.syncly.dto.ColumnDTO;
import com.syncly.syncly.entity.ColumnEntity;
import com.syncly.syncly.mapper.ColumnMapper;
import com.syncly.syncly.service.ColumnService;

@RestController
@RequestMapping("/columns")
public class ColumnController extends BaseController<ColumnEntity, ColumnDTO, UUID> {

    public ColumnController(ColumnService service) {
        super(service, ColumnMapper::toDTO);
    }
}
