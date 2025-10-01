package com.syncly.syncly.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.syncly.syncly.dto.LoginResponseDTO;
import com.syncly.syncly.entity.User;
import com.syncly.syncly.infrastructure.security.JwtUtil;
import com.syncly.syncly.service.AuthService;
import com.syncly.syncly.service.UserService;
import com.syncly.syncly.wrapper.ServiceResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                           UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public ServiceResponse<User> register(User user) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("email", user.getEmail());
        List<User> users = userService.findAllByFields(filter, null, null);
        log.info("Users found with email {}: {}", user.getEmail(), users);
        ServiceResponse<User> serviceResponse = new ServiceResponse<>();
        if (users != null && !users.isEmpty()) {
            serviceResponse.setData(null);
            serviceResponse.setStatus(ServiceResponse.ResStatus.ERROR);
            serviceResponse.setMessage("Email already in use");
            return serviceResponse;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            serviceResponse.setData(userService.create(user));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        serviceResponse.setStatus(ServiceResponse.ResStatus.SUCCESS);
        serviceResponse.setMessage("User registered successfully");
        return serviceResponse;
    }

    @Override
    public ServiceResponse<LoginResponseDTO> login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email/password");
        }

        Map<String, Object> filter = new HashMap<>();
        filter.put("email", email);
        List<User> users = userService.findAllByFields(filter, null, null);
        ServiceResponse<LoginResponseDTO> serviceResponse = new ServiceResponse<>();

        if (users == null || users.isEmpty()) {
            serviceResponse.setData(null);
            serviceResponse.setStatus(ServiceResponse.ResStatus.ERROR);
            serviceResponse.setMessage("User not found");
            return serviceResponse;
        }

        User user = users.get(0);
        String token = jwtUtil.generateToken(user.getEmail());
        LoginResponseDTO loginResponse = new LoginResponseDTO(user, token);
        serviceResponse.setData(loginResponse);
        serviceResponse.setStatus(ServiceResponse.ResStatus.SUCCESS);
        serviceResponse.setMessage("Login successful");
        return serviceResponse;
    }
}
