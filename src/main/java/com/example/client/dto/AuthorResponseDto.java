package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    @Override
    public String toString() {
        if (firstName == null && lastName == null) return "Unknown Author";
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}