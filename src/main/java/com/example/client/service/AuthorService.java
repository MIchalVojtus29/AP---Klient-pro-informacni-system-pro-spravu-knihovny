package com.example.client.service;

import com.example.client.controller.api.AuthorApiClient;
import com.example.client.dto.AuthorRequestDto;
import com.example.client.dto.AuthorResponseDto;
import java.util.List;

/**
 * Service for managing authors within a client application.
 */
public class AuthorService {
    private final AuthorApiClient authorApiClient = new AuthorApiClient();

    public List<AuthorResponseDto> fetchAllAuthors() throws Exception {
        return authorApiClient.getAllAuthors();
    }
    public void saveAuthor(AuthorRequestDto dto) throws Exception {
        authorApiClient.createAuthor(dto);
    }
}