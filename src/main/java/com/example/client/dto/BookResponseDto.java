package com.example.client.dto;

import lombok.Data;

@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String authorName;
    private String genreName;
    private Integer releaseYear;
    private Integer quantity;
}