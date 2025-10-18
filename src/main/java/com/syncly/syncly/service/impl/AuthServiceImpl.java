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
        ServiceResponse<User> response = new ServiceResponse<>();

        try {
            Map<String, Object> filter = new HashMap<>();
            filter.put("email", user.getEmail());
            ServiceResponse<List<User>> existingUsersResp = userService.findAllByFields(filter, null, null);

            if (existingUsersResp.getData() != null && !existingUsersResp.getData().isEmpty()) {
                response.setStatus(ServiceResponse.ResStatus.ERROR);
                response.setMessage("Email already in use");
                response.setData(null);
                return response;
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userService.create(user).getData();

            response.setData(savedUser);
            response.setStatus(ServiceResponse.ResStatus.SUCCESS);
            response.setMessage("User registered successfully");

        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Error registering user", e);
            response.setStatus(ServiceResponse.ResStatus.ERROR);
            response.setMessage("Error registering user: " + e.getMessage());
            response.setData(null);
        }

        return response;
    }

    @Override
    public ServiceResponse<LoginResponseDTO> login(String email, String password) {
        ServiceResponse<LoginResponseDTO> response = new ServiceResponse<>();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            response.setStatus(ServiceResponse.ResStatus.ERROR);
            response.setMessage("Invalid email/password");
            response.setData(null);
            return response;
        }

        Map<String, Object> filter = new HashMap<>();
        filter.put("email", email);
        ServiceResponse<List<User>> usersResp = userService.findAllByFields(filter, null, null);

        List<User> users = usersResp.getData();
        if (users == null || users.isEmpty()) {
            response.setStatus(ServiceResponse.ResStatus.ERROR);
            response.setMessage("User not found");
            response.setData(null);
            return response;
        }

        User user = users.get(0);
        String token = jwtUtil.generateToken(user.getEmail());
        response.setData(new LoginResponseDTO(user, token));
        response.setStatus(ServiceResponse.ResStatus.SUCCESS);
        response.setMessage("Login successful");

        return response;
    }

    @Override
    public ServiceResponse<User> validateToken(String token) {
        ServiceResponse<User> response = new ServiceResponse<>();

        try {
            String email = jwtUtil.extractUsername(token);

            Map<String, Object> filter = new HashMap<>();
            filter.put("email", email);
            ServiceResponse<List<User>> usersResp = userService.findAllByFields(filter, null, null);
            List<User> users = usersResp != null ? usersResp.getData() : null;
            log.info("users: {}", users);

            if (users == null || users.isEmpty()) {
                response.setStatus(ServiceResponse.ResStatus.ERROR);
                response.setMessage("Invalid token: user not found");
                response.setData(null);
                return response;
            }

            User user = users.get(0);
            boolean isValid = jwtUtil.validateToken(token, user.getEmail());

            if (!isValid) {
                response.setStatus(ServiceResponse.ResStatus.ERROR);
                response.setMessage("Invalid or expired token");
                response.setData(null);
                return response;
            }

            response.setStatus(ServiceResponse.ResStatus.SUCCESS);
            response.setMessage("Token is valid");
            response.setData(user);

        } catch (Exception e) {
            log.error("Error validating token", e);
            response.setStatus(ServiceResponse.ResStatus.ERROR);
            response.setMessage("Error validating token: " + e.getMessage());
            response.setData(null);
        }

        return response;
    }

}
