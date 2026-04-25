package com.example.client.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanCreateDto {
    private Integer bookId;
    private Integer readerId;
    private Integer librarianId;
    private LocalDate returnDate;
}