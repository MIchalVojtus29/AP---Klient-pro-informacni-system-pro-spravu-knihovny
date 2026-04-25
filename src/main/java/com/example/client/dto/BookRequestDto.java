package com.example.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    private String title;
    private Integer releaseYear;
    private Long authorId;
    private Long genreId;
    private Integer quantity;
}