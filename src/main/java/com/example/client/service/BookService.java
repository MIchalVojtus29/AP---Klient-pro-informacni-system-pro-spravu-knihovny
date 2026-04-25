package com.example.client.service;

import com.example.client.controller.api.AuthorApiClient;
import com.example.client.controller.api.BookApiClient;
import com.example.client.dto.AuthorResponseDto;
import com.example.client.dto.BookRequestDto;
import com.example.client.dto.BookResponseDto;
import java.util.List;

/**
 * Service for managing books within a client application.
 */
public class BookService {

    private final BookApiClient bookApiClient;

    public BookService() {
        this.bookApiClient = new BookApiClient();
    }
    public List<BookResponseDto> fetchAllBooks(int page, int size) throws Exception {
        return bookApiClient.getAllBooks(page, size);
    }
    public void saveBook(BookRequestDto dto) throws Exception {
        bookApiClient.createBook(dto);
    }
    public void updateBook(Long id, BookRequestDto dto) throws Exception {
        bookApiClient.updateBook(id, dto);
    }
    public void removeBook(Long id) throws Exception {
        bookApiClient.deleteBook(id);
    }
}

