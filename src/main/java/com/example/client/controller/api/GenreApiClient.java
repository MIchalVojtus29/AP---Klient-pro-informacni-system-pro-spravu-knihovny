package com.example.client.controller.api;

import com.example.client.dto.ErrorResponseDto;
import com.example.client.dto.GenreRequestDto;
import com.example.client.dto.GenreResponseDto;
import com.example.client.dto.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GenreApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/genres";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<GenreResponseDto> getAllGenres() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<GenreResponseDto>>() {}
            );
        } else {
            throw new RuntimeException("Error loading genres: " + response.statusCode());
        }
    }

    public void createGenre(GenreRequestDto genreRequest) throws Exception {
        String json = objectMapper.writeValueAsString(genreRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Error saving genre: " + response.statusCode());
        }
    }
}