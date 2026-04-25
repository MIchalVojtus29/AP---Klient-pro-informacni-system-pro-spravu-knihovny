package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDto {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private Map<String, String> validationErrors;

    public String getMessage() {
        return detail;
    }
}