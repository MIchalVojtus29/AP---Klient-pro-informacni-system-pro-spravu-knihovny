package com.example.client.controller;

import com.example.client.dto.UserResponseDto;
import com.example.client.util.LanguageManager;
import com.example.client.util.UserSession;
import com.example.client.util.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the main application shell.
 * Manages the top bar, sidebar navigation, language switching, and logout.
 */
public class MainController {

    @FXML private Label userNameLabel;
    @FXML private Button btnLang;
    @FXML private BorderPane mainLayout;
    @FXML private ResourceBundle resources;

    @FXML
    public void initialize() {
        setupUserInterface();
        handleShowBooks();
    }

    private void setupUserInterface() {
        UserResponseDto user = UserSession.getLoggedUser();
        if (user != null) {
            userNameLabel.setText(user.getFirstName() + " " + user.getLastName() + " (" + user.getRole() + ")");
        }
    }

    private void loadView(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());
            Node content = loader.load();

            mainLayout.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowBooks() {
        loadView(View.BOOK_LIST);
    }

    @FXML
    private void handleShowUsers() {
        loadView(View.USER_LIST);
    }
    @FXML
    private void handleShowLoans() {
        loadView(View.LOAN_LIST);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.clear();
        try {
            switchScene(View.LOGIN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLanguageSwitch(ActionEvent event) {
        toggleLocale();
        try {
            switchScene(View.MAIN);
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

    private void switchScene(View view) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
        loader.setResources(LanguageManager.getBundle());
        Scene scene = new Scene(loader.load(), view.getWidth(), view.getHeight());

        Stage stage = (Stage) userNameLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}