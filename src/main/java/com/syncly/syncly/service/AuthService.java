package com.syncly.syncly.service;

import com.syncly.syncly.dto.LoginResponseDTO;
import com.syncly.syncly.entity.User;
import com.syncly.syncly.wrapper.ServiceResponse;

public interface AuthService {
    ServiceResponse<LoginResponseDTO> login(String email, String password);
    ServiceResponse<User> register(User user);
}
