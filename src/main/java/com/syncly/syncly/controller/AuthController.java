package com.syncly.syncly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.syncly.syncly.dto.LoginDTO;
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
    public ResponseEntity<ServiceResponse<LoginResponseDTO>> login(@RequestBody LoginDTO request) {
        log.info("User login attempt with email: {}", request.getEmail());
        // return ResponseEntity.status(Response.SC_NOT_IMPLEMENTED).build();
        ServiceResponse<LoginResponseDTO> response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ServiceResponse<User>> validateToken(@RequestParam(required = false) String token,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Validating token...");

        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        } else if (token != null) {
            jwtToken = token;
        }

        if (jwtToken == null || jwtToken.isEmpty()) {
            ServiceResponse<User> errorResponse = new ServiceResponse<>();
            errorResponse.setStatus(ServiceResponse.ResStatus.ERROR);
            errorResponse.setMessage("Token not provided");
            errorResponse.setData(null);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        ServiceResponse<User> response = authService.validateToken(jwtToken);
        return ResponseEntity.ok(response);
    }

}
