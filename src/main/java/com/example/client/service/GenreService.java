package com.example.client.service;

import com.example.client.controller.api.GenreApiClient;
import com.example.client.dto.GenreRequestDto;
import com.example.client.dto.GenreResponseDto;
import java.util.List;

public class GenreService {
    private final GenreApiClient genreApiClient = new GenreApiClient();

    public List<GenreResponseDto> fetchAllGenres() throws Exception {
        return genreApiClient.getAllGenres();
    }
    public void saveGenre(GenreRequestDto dto) throws Exception {
        genreApiClient.createGenre(dto);
    }
}