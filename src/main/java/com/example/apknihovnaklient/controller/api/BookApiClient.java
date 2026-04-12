package com.example.apknihovnaklient.controller.api;

import com.example.apknihovnaklient.dto.BookResponseDto;
import com.example.apknihovnaklient.dto.ErrorResponseDto;
import com.example.apknihovnaklient.dto.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Client for communicating with the book-related API endpoints.
 */
public class BookApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/books";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BookApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetches all books from the server.
     * @return List of BookResponseDto
     * @throws Exception if server returns error or connection fails
     */
    public List<BookResponseDto> getAllBooks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            PageResponse<BookResponseDto> page = objectMapper.readValue(
                    response.body(),
                    new TypeReference<PageResponse<BookResponseDto>>() {}
            );
            return page.getContent();
        } else {
            System.err.println("SERVER ERROR - Status: " + response.statusCode() + ", Body: " + response.body());
            try {
                ErrorResponseDto error = objectMapper.readValue(response.body(), ErrorResponseDto.class);
                throw new RuntimeException(error.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Failed to load books. Status: " + response.statusCode());
            }
        }
    }
}