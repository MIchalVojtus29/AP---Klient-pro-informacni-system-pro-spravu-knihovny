package com.example.client.controller;

import com.example.client.dto.AuthorResponseDto;
import com.example.client.dto.BookRequestDto;
import com.example.client.dto.BookResponseDto;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class EditBookController {

    @FXML private TextField titleField;
    @FXML private TextField yearField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<AuthorResponseDto> authorCombo;
    @FXML private ComboBox<GenreResponseDto> genreCombo;
    @FXML private ResourceBundle resources;

    private final BookService bookService = new BookService();
    private final AuthorService authorService = new AuthorService();
    private final GenreService genreService = new GenreService();

    private Long currentBookId;
    private String initialAuthorName;
    private String initialGenreName;

    @FXML
    public void initialize() {
        loadComboBoxData();
    }

    public void setBookData(BookResponseDto book) {
        this.currentBookId = book.getId();
        this.initialAuthorName = book.getAuthorName();
        this.initialGenreName = book.getGenreName();

        titleField.setText(book.getTitle());
        yearField.setText(String.valueOf(book.getReleaseYear()));
        quantityField.setText(String.valueOf(book.getQuantity()));

        selectInitialValues();
    }

    private void loadComboBoxData() {
        AsyncManager.runAsync(this::fetchAuthorsTask, this::handleAuthorsSuccess, this::handleLoadError);
        AsyncManager.runAsync(this::fetchGenresTask, this::handleGenresSuccess, this::handleLoadError);
    }

    @FXML
    private void handleSave() {
        if (isInputInvalid()) {
            showError(resources.getString("error.validation.title"), resources.getString("error.validation.content"));
            return;
        }

        try {
            BookRequestDto request = createRequest();
            AsyncManager.runAsync(() -> performUpdateTask(request), this::handleUpdateSuccess, this::handleUpdateError);
        } catch (NumberFormatException e) {
            showError(resources.getString("error.validation.title"), resources.getString("book.error.invalidNumbers"));
        }
    }

    @FXML
    private void handleNewAuthor() {
        openModal(View.ADD_AUTHOR, resources.getString("author.add.title"));
    }

    @FXML
    private void handleNewGenre() {
        openModal(View.ADD_GENRE, resources.getString("genre.add.title"));
    }
    private void openModal(View view, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load(), view.getWidth(), view.getHeight()));

            stage.showAndWait();

            loadComboBoxData();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to open window: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private List<AuthorResponseDto> fetchAuthorsTask() {
        try {
            return authorService.fetchAllAuthors();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<GenreResponseDto> fetchGenresTask() {
        try {
            return genreService.fetchAllGenres();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private Void performUpdateTask(BookRequestDto request) {
        try {
            bookService.updateBook(currentBookId, request);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleAuthorsSuccess(List<AuthorResponseDto> authors) {
        authorCombo.setItems(FXCollections.observableArrayList(authors));
        selectInitialValues();
    }

    private void handleGenresSuccess(List<GenreResponseDto> genres) {
        genreCombo.setItems(FXCollections.observableArrayList(genres));
        selectInitialValues();
    }

    private void selectInitialValues() {
        if (authorCombo.getItems() != null && initialAuthorName != null) {
            authorCombo.getItems().stream()
                    .filter(a -> (a.getFirstName() + " " + a.getLastName()).equals(initialAuthorName))
                    .findFirst()
                    .ifPresent(authorCombo::setValue);
        }
        if (genreCombo.getItems() != null && initialGenreName != null) {
            genreCombo.getItems().stream()
                    .filter(g -> g.getName().equals(initialGenreName))
                    .findFirst()
                    .ifPresent(genreCombo::setValue);
        }
    }

    private BookRequestDto createRequest() {
        return new BookRequestDto(
                titleField.getText().trim(),
                Integer.parseInt(yearField.getText().trim()),
                authorCombo.getValue().getId(),
                genreCombo.getValue().getId(),
                Integer.parseInt(quantityField.getText().trim())
        );
    }

    private boolean isInputInvalid() {
        return titleField.getText().isEmpty() || authorCombo.getValue() == null || genreCombo.getValue() == null;
    }

    private void handleUpdateSuccess(Object res) {
        closeWindow();
    }

    private void handleUpdateError(Throwable ex) {
        showError(resources.getString("error.save.title"), ex.getMessage());
    }

    private void handleLoadError(Throwable ex) {
        System.err.println("Loading error: " + ex.getMessage());
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