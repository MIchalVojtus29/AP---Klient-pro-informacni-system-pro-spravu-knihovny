package com.example.client.controller;

import com.example.client.dto.UserCreateDto;
import com.example.client.dto.UserResponseDto;
import com.example.client.service.UserService;
import com.example.client.util.AsyncManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EditUserController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private ResourceBundle resources;

    private final UserService userService = new UserService();
    private final Map<String, String> roleMap = new LinkedHashMap<>();
    private Long userId;

    @FXML
    public void initialize() {
        roleMap.put(resources.getString("role.admin"), "admin");
        roleMap.put(resources.getString("role.librarian"), "librarian");
        roleMap.put(resources.getString("role.reader"), "reader");

        roleCombo.getItems().addAll(roleMap.keySet());
    }

    public void setUserData(UserResponseDto user) {
        this.userId = Long.valueOf(user.getId());
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());

        for (Map.Entry<String, String> entry : roleMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(user.getRole())) {
                roleCombo.setValue(entry.getKey());
                break;
            }
        }
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

        String selectedRoleText = roleCombo.getValue();
        request.setRole(roleMap.get(selectedRoleText));

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
        closeWindow();
    }

    private void handleSaveError(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to edit user");
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
        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Missing credentials");
            alert.setContentText("Please fill in all required fields");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}