package com.syncly.syncly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.dto.LoginResponseDTO;
import com.syncly.syncly.entity.User;
import com.syncly.syncly.service.AuthService;
import com.syncly.syncly.wrapper.ServiceResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ServiceResponse<User>> register(@RequestBody User user) {
        log.info("Registering user with email: {}", user.getEmail());
        ServiceResponse<User> registeredUser = authService.register(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<LoginResponseDTO>> login(@RequestBody LoginRequest request) {
        log.info("User login attempt with email: {}", request.getEmail());
        // return ResponseEntity.status(Response.SC_NOT_IMPLEMENTED).build();
        ServiceResponse<LoginResponseDTO> response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
