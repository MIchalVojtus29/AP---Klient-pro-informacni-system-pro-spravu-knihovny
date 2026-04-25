package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanResponseDto {
    private Long id;
    private String bookTitle;
    private String readerName;
    private String librarianName;
    private LocalDate loanDate;

    @JsonProperty("returnDate")
    private LocalDate dueDate;
    @JsonProperty("actualReturnDate")
    private LocalDate actualReturnDate;
    @JsonProperty("loanState")
    private String status;

    @JsonProperty("book")
    private void unpackBook(Map<String, Object> book) {
        if (book != null) {
            this.bookTitle = (String) book.get("title");
        }
    }

    @JsonProperty("reader")
    private void unpackReader(Map<String, Object> reader) {
        if (reader != null) {
            this.readerName = reader.get("firstName") + " " + reader.get("lastName");
        }
    }

    @JsonProperty("librarian")
    private void unpackLibrarian(Map<String, Object> librarian) {
        if (librarian != null) {
            this.librarianName = librarian.get("firstName") + " " + librarian.get("lastName");
        }
    }
}