package com.example.apknihovnaklient.controller;

import com.example.apknihovnaklient.controller.api.AuthApiClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for the login window.
 * Handles user input, asynchronous authentication, and UI updates.
 *
 * @author Michal Vojtuš
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button loginButton;

    private final AuthApiClient authApiClient;

    public LoginController() {
        this.authApiClient = new AuthApiClient();
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both email and password.", true);
            return;
        }

        loginButton.setDisable(true);
        showMessage("Logging in...", false);

        CompletableFuture.supplyAsync(() -> {
            try {
                return authApiClient.login(email, password);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }).thenAccept(userProfile -> Platform.runLater(() -> {
            showMessage("Login successful! Welcome " + userProfile.getFirstName(), false);
            loginButton.setDisable(false);
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                showMessage("Login failed: " + ex.getCause().getMessage(), true);
                loginButton.setDisable(false);
            });
            return null;
        });
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setTextFill(isError ? Color.RED : Color.web("#2c3e50"));
    }
}