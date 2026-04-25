package com.example.client.controller.api;

import com.example.client.dto.BookRequestDto;
import com.example.client.dto.BookResponseDto;
import com.example.client.dto.ErrorResponseDto;
import com.example.client.dto.PageResponse;
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
    public List<BookResponseDto> getAllBooks(int page, int size) throws Exception {
        String urlWithParams = String.format("%s?page=%d&size=%d", BASE_URL, page, size);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            PageResponse<BookResponseDto> pageResponse = objectMapper.readValue(
                    response.body(),
                    new TypeReference<PageResponse<BookResponseDto>>() {}
            );
            return pageResponse.getContent();
        } else {
            try {
                ErrorResponseDto error = objectMapper.readValue(response.body(), ErrorResponseDto.class);
                throw new RuntimeException(error.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Failed to load books. Status: " + response.statusCode());
            }
        }
    }

    public void createBook(BookRequestDto bookRequest) throws Exception {
        String json = objectMapper.writeValueAsString(bookRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Error while saving: " + response.statusCode());
        }
    }
    public void updateBook(Long id, BookRequestDto bookRequest) throws Exception {
        String json = objectMapper.writeValueAsString(bookRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error while editing: " + response.statusCode());
        }
    }
    public void deleteBook(Long id) throws Exception {
        String finalUrl = BASE_URL + "/" + id;
        System.out.println("Mazání na URL: " + finalUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Error while deleting on the server: " + response.statusCode());
        }
    }
}