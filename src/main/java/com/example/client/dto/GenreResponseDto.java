package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreResponseDto {

    private Long id;
    private String name;

    @Override
    public String toString() {
        return name != null ? name : "Unknown Genre";
    }
}