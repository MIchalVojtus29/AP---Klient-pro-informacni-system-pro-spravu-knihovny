package com.example.client.controller.api;

import com.example.client.dto.ErrorResponseDto;
import com.example.client.dto.LoginRequestDto;
import com.example.client.dto.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Client for communicating with the authentication API endpoints.
 * Handles user login and session initialization.
 *
 * @author Michal Vojtuš
 */
public class AuthApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/auth";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    public UserResponseDto login(String email, String password) throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto(email, password);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), UserResponseDto.class);
        } else {
            try {
                ErrorResponseDto errorDto = objectMapper.readValue(response.body(), ErrorResponseDto.class);
                throw new RuntimeException(errorDto.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Error communicating with the server. Status:" + response.statusCode());
            }
        }
    }
}