package com.example.apknihovnaklient.dto;

import lombok.Data;

@Data
public class BookResponseDto {
    private Integer id;
    private String title;
    private String authorName;
    private String genreName;
    private Integer releaseYear;
    private Integer quantity;
}