package com.example.client.controller;

import com.example.client.controller.api.AuthApiClient;
import com.example.client.dto.UserResponseDto;
import com.example.client.service.AuthService;
import com.example.client.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the login window.
 * Handles user input, asynchronous authentication, and UI updates.
 *
 * @author Michal Vojtuš
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button loginButton;
    @FXML private Button btnLang;
    @FXML private ResourceBundle resources;

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (isInputInvalid(email, password)) return;

        prepareUiForLoggingIn();

        AsyncManager.runAsync(
                () -> performLoginTask(email, password),
                this::handleLoginSuccess,
                this::handleLoginError
        );
    }

    @FXML
    public void handleLanguageSwitch(ActionEvent event) {
        toggleLocale();
        try {
            switchScene(View.LOGIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toggleLocale() {
        if (LanguageManager.getCurrentLocale().getLanguage().equals("cs")) {
            LanguageManager.setLocale(new Locale("en", "US"));
        } else {
            LanguageManager.setLocale(new Locale("cs", "CZ"));
        }
    }

    private UserResponseDto performLoginTask(String email, String password) {
        try {
            return authService.authenticate(email, password);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleLoginSuccess(UserResponseDto userProfile) {
        try {
            SessionManager.setLoggedInUser(userProfile);

            switchScene(View.MAIN);
        } catch (IOException e) {
            handleLoginError(e);
        }
    }

    private void handleLoginError(Throwable ex) {
        showMessage(resources.getString("login.error.failed") + ex.getMessage(), true);
        loginButton.setDisable(false);
    }

    private boolean isInputInvalid(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showMessage(resources.getString("login.error.empty"), true);
            return true;
        }
        return false;
    }

    private void prepareUiForLoggingIn() {
        loginButton.setDisable(true);
        showMessage(resources.getString("login.info.loggingIn"), false);
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setTextFill(isError ? Color.RED : Color.web("#2c3e50"));
    }

    private void switchScene(View view) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
        loader.setResources(LanguageManager.getBundle());
        Scene scene = new Scene(loader.load(), view.getWidth(), view.getHeight());
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}