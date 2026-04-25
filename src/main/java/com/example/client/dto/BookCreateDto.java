package com.example.client.dto;

import lombok.Data;

@Data
public class BookCreateDto {
    private String title;
    private Integer releaseYear;
    private Integer quantity;
    private Integer authorId;
    private Integer genreId;
}