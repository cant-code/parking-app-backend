package com.mini.ecommerceapp.controllers;

import com.mini.ecommerceapp.dto.UserDTO;
import com.mini.ecommerceapp.services.AuthService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

//TODO: Registration
@RestController
public class AppController {
    private final AuthService authService;

    @Autowired
    public AppController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.authenticateUser(userDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser() {
        return ResponseEntity.created(URI.create(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString())).build();
    }

    @GetMapping("/token/refresh")
    public AccessTokenResponse refreshToken(@RequestParam String token) {
        return authService.refreshToken(token);
    }
}
