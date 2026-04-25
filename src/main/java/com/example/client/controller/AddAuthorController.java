package com.example.client.controller;

import com.example.client.dto.AuthorRequestDto;
import com.example.client.service.AuthorService;
import com.example.client.util.AsyncManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class AddAuthorController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField nationalityField;
    @FXML private ResourceBundle resources;

    private final AuthorService authorService = new AuthorService();

    @FXML
    private void handleSave() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            showError("Validation", "First and last name are required.");
            return;
        }

        AuthorRequestDto request = new AuthorRequestDto(
                firstNameField.getText(),
                lastNameField.getText(),
                nationalityField.getText()
        );

        AsyncManager.runAsync(
                () -> performSaveTask(request),
                this::handleSaveSuccess,
                this::handleSaveError
        );
    }

    private Void performSaveTask(AuthorRequestDto request) {
        try {
            authorService.saveAuthor(request);
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
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}