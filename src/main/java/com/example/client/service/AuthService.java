package com.example.client.service;

import com.example.client.controller.api.AuthApiClient;
import com.example.client.dto.UserResponseDto;
import com.example.client.util.UserSession;

/**
 * Service handling authentication-related business logic on the client side.
 */
public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService() {
        this.authApiClient = new AuthApiClient();
    }

    public UserResponseDto authenticate(String email, String password) throws Exception {
        UserResponseDto user = authApiClient.login(email, password);
        UserSession.setLoggedUser(user);
        return user;
    }
}