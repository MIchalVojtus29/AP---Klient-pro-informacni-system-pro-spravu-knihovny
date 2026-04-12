package com.example.apknihovnaklient.controller;

import com.example.apknihovnaklient.controller.api.AuthApiClient;
import com.example.apknihovnaklient.util.AsyncManager;
import com.example.apknihovnaklient.util.LanguageManager;
import com.example.apknihovnaklient.util.UserSession;
import javafx.application.Platform;
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

    @FXML
    private Button btnLang;
    @FXML
    private ResourceBundle resources;

    private final AuthApiClient authApiClient;

    public LoginController() {
        this.authApiClient = new AuthApiClient();
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(resources.getString("login.error.empty"), true);
            return;
        }

        loginButton.setDisable(true);
        showMessage(resources.getString("login.info.loggingIn"), false);

        AsyncManager.runAsync(
                () -> {
                    try {
                        return authApiClient.login(email, password);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                },
                userProfile -> {
                    UserSession.setLoggedUser(userProfile);
                    try {
                        switchScene("/com/example/apknihovnaklient/Main.fxml", 900, 600);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                ex -> {
                    showMessage(resources.getString("login.error.failed") + ex.getMessage(), true);
                    loginButton.setDisable(false);
                }
        );
    }

    @FXML
    public void handleLanguageSwitch(ActionEvent event) {
        if (LanguageManager.getCurrentLocale().getLanguage().equals("cs")) {
            LanguageManager.setLocale(new Locale("en", "US"));
        } else {
            LanguageManager.setLocale(new Locale("cs", "CZ"));
        }

        reloadView();
    }

    private void reloadView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/apknihovnaklient/Login.fxml"));
            loader.setResources(LanguageManager.getBundle());

            Parent root = loader.load();
            Stage stage = (Stage) btnLang.getScene().getWindow();
            stage.setScene(new Scene(root, 350, 400));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setTextFill(isError ? Color.RED : Color.web("#2c3e50"));
    }

    private void switchScene(String fxmlPath, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setResources(LanguageManager.getBundle());

        Scene scene = new Scene(loader.load(), width, height);
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}