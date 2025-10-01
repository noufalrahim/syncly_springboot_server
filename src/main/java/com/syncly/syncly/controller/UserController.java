package com.syncly.syncly.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.controller.base.BaseController;
import com.syncly.syncly.entity.User;
import com.syncly.syncly.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User, Object, UUID> {
    public UserController(UserService service) {
        super(service);
    }
}
