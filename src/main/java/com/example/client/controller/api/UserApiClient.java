package com.example.client.controller.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class UserApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/users";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String fetchUsersJson(int page, int size, String search) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL)
                .append("?page=").append(page)
                .append("&size=").append(size);

        if (search != null && !search.trim().isEmpty()) {
            String encodedSearch = URLEncoder.encode(search.trim(), StandardCharsets.UTF_8);
            urlBuilder.append("&search=").append(encodedSearch);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error loading users from the server. Status: " + response.statusCode());
        }

        return response.body();
    }

    public void createUserJson(String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new RuntimeException("Error saving to server. Status: " + response.statusCode() + " " + response.body());
        }
    }

    public void updateUserJson(Long id, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Error editing user. Status: " + response.statusCode());
        }
    }

    public void deleteUser(Integer id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Error while deleting on the server. Status: " + response.statusCode());
        }
    }

}