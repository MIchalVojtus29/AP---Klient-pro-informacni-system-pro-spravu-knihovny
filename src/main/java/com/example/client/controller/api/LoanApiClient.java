package com.example.client.controller.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoanApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/loans";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String fetchLoansJson(int page, int size) throws Exception {
        String url = String.format("%s?page=%d&size=%d", BASE_URL, page, size);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public void returnLoan(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/return"))
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new RuntimeException("Error returning book. Status: " + response.statusCode());
        }
    }

    public void createLoanJson(String json) throws Exception {
        System.out.println("Odesílám JSON: " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Server error " + response.statusCode() + ": " + response.body());
        }
    }
}