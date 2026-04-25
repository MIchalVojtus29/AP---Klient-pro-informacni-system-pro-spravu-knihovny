package com.example.client.controller;

import com.example.client.dto.GenreRequestDto;
import com.example.client.service.GenreService;
import com.example.client.util.AsyncManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddGenreController {

    @FXML private TextField nameField;
    @FXML private ResourceBundle resources;

    private final GenreService genreService = new GenreService();

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Validation", "The genre name cannot be empty.");
            return;
        }

        GenreRequestDto request = new GenreRequestDto(name);

        AsyncManager.runAsync(
                () -> performSaveTask(request),
                this::handleSaveSuccess,
                this::handleSaveError
        );
    }

    private Void performSaveTask(GenreRequestDto request) {
        try {
            genreService.saveGenre(request);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleSaveSuccess(Object res) {
        closeWindow();
    }

    private void handleSaveError(Throwable ex) {
        showError("Error while saving", ex.getMessage());
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}