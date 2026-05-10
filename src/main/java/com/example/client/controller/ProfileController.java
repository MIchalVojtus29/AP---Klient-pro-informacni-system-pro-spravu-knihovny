package com.example.client.controller;

import com.example.client.dto.UserCreateDto;
import com.example.client.dto.UserResponseDto;
import com.example.client.service.UserService;
import com.example.client.util.AsyncManager;
import com.example.client.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class ProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ResourceBundle resources;

    private final UserService userService = new UserService();
    private Long userId;

    @FXML
    public void initialize() {
    }
    public void setUserData(UserResponseDto user) {
        this.userId = Long.valueOf(user.getId());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());

        phoneField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,9}")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) return;
        UserCreateDto request = new UserCreateDto();
        request.setFirstName(firstNameField.getText().trim());
        request.setLastName(lastNameField.getText().trim());
        request.setEmail(emailField.getText().trim());
        request.setPhoneNumber(phoneField.getText().trim());

        String pass = passwordField.getText().trim();
        request.setPassword(pass.isEmpty() ? null : pass);

        request.setRole(SessionManager.getLoggedInUser().getRole());

        AsyncManager.runAsync(
                () -> performSaveTask(request),
                this::handleSaveSuccess,
                this::handleSaveError
        );
    }

    private Void performSaveTask(UserCreateDto request) {
        try {
            userService.updateUser(userId, request);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleSaveSuccess(Void result) {
        UserResponseDto loggedUser = SessionManager.getLoggedInUser();
        if (loggedUser != null) {
            loggedUser.setEmail(emailField.getText().trim());
            loggedUser.setPhoneNumber(phoneField.getText().trim());
        }

        closeWindow();
    }

    private void handleSaveError(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resources.getString("error.title"));
        alert.setHeaderText(resources.getString("profile.save.failed"));
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        if (emailField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(resources.getString("validation.failed"));
            alert.setContentText(resources.getString("validation.email.required"));
            alert.showAndWait();
            return false;
        }

        String phone = phoneField.getText().trim();
        if (!phone.matches("\\d{9}")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(resources.getString("validation.failed"));
            alert.setContentText(resources.getString("validation.phone.invalid"));
            alert.showAndWait();
            return false;
        }

        return true;
    }
}