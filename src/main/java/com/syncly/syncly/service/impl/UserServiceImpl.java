package com.syncly.syncly.service.impl;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.syncly.syncly.entity.User;
import com.syncly.syncly.repository.UserRepository;
import com.syncly.syncly.service.UserService;
import com.syncly.syncly.service.impl.base.BaseServiceImpl;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService{
    public UserServiceImpl(UserRepository repository, JpaSpecificationExecutor<User> specRepository){
        super(repository, specRepository, User.class);
    }
}
    