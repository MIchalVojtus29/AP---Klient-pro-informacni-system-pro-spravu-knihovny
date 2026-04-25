package com.example.client.controller;

import com.example.client.dto.AuthorResponseDto;
import com.example.client.dto.BookRequestDto;
import com.example.client.dto.GenreResponseDto;
import com.example.client.service.AuthorService;
import com.example.client.service.BookService;
import com.example.client.service.GenreService;
import com.example.client.util.AsyncManager;
import com.example.client.util.LanguageManager;
import com.example.client.util.View;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class AddBookController {

    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private ComboBox<AuthorResponseDto> authorCombo;
    @FXML private ComboBox<GenreResponseDto> genreCombo;
    @FXML private ResourceBundle resources;

    private final BookService bookService = new BookService();
    private final AuthorService authorService = new AuthorService();
    private final GenreService genreService = new GenreService();

    @FXML
    public void initialize() {
        loadData();
    }
    private void loadData() {
        AsyncManager.runAsync(this::fetchAuthorsTask, this::handleAuthorsLoadSuccess, this::handleLoadError);
        AsyncManager.runAsync(this::fetchGenresTask, this::handleGenresLoadSuccess, this::handleLoadError);
    }

    @FXML
    private void handleSave() {
        if (isInputInvalid()) {
            showError(resources.getString("error.validation.title"), resources.getString("error.validation.content"));
            return;
        }

        BookRequestDto request = createRequestFromForm();
        AsyncManager.runAsync(() -> performSaveTask(request), this::handleSaveSuccess, this::handleSaveError);
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleNewAuthor() {
        openModal(View.ADD_AUTHOR, resources.getString("author.add.title"));
    }

    @FXML
    private void handleNewGenre() {
        openModal(View.ADD_GENRE, resources.getString("genre.add.title"));
    }

    private List<AuthorResponseDto> fetchAuthorsTask() {
        try { return authorService.fetchAllAuthors(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private List<GenreResponseDto> fetchGenresTask() {
        try { return genreService.fetchAllGenres(); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private Void performSaveTask(BookRequestDto request) {
        try {
            bookService.saveBook(request);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleAuthorsLoadSuccess(List<AuthorResponseDto> authors) {
        authorCombo.setItems(FXCollections.observableArrayList(authors));
    }

    private void handleGenresLoadSuccess(List<GenreResponseDto> genres) {
        genreCombo.setItems(FXCollections.observableArrayList(genres));
    }

    private void handleSaveSuccess(Object result) {
        closeWindow();
    }

    private void handleSaveError(Throwable ex) {
        showError(resources.getString("error.save.title"), ex.getMessage());
    }

    private void handleLoadError(Throwable ex) {
        System.err.println("Error loading code lists: " + ex.getMessage());
    }

    private boolean isInputInvalid() {
        return titleField.getText().isEmpty() || authorCombo.getValue() == null || genreCombo.getValue() == null;
    }

    private BookRequestDto createRequestFromForm() {
        BookRequestDto dto = new BookRequestDto();
        dto.setTitle(titleField.getText().trim());
        try {
            dto.setReleaseYear(Integer.parseInt(yearField.getText().trim()));
        } catch (NumberFormatException e) {
            dto.setReleaseYear(0);
        }
        dto.setAuthorId(authorCombo.getValue().getId());
        dto.setGenreId(genreCombo.getValue().getId());
        dto.setQuantity(1);
        return dto;
    }

    private void openModal(View view, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load(), view.getWidth(), view.getHeight()));

            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}