package com.example.client.service;

import com.example.client.controller.api.LoanApiClient;
import com.example.client.dto.LoanCreateDto;
import com.example.client.dto.LoanResponseDto;
import com.example.client.dto.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LoanService {
    private final LoanApiClient apiClient = new LoanApiClient();
    private final ObjectMapper objectMapper;

    public LoanService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public PageResponse<LoanResponseDto> getLoans(int page, int size) throws Exception {
        String json = apiClient.fetchLoansJson(page, size);

        java.util.List<LoanResponseDto> list = objectMapper.readValue(json,
                new TypeReference<java.util.List<LoanResponseDto>>() {});

        PageResponse<LoanResponseDto> response = new PageResponse<>();
        response.setContent(list);
        response.setTotalPages(1);
        return response;
    }

    public void returnBook(Long id) throws Exception {
        apiClient.returnLoan(id);
    }
    public void createLoan(LoanCreateDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        apiClient.createLoanJson(json);
    }
}