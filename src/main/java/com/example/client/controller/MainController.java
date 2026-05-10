package com.example.client.controller;

import com.example.client.dto.UserResponseDto;
import com.example.client.util.LanguageManager;
import com.example.client.util.SessionManager;
import com.example.client.util.UserSession;
import com.example.client.util.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
    private Button btnUsers;
    @FXML
    private Button btnLoans;
    @FXML
    private Button btnCatalog;

    @FXML
    public void initialize() {
        setupUserInterface();
        handleShowBooks();

        String role = SessionManager.getLoggedInUser().getRole();

        btnUsers.setVisible(role.equalsIgnoreCase("admin"));
        btnUsers.managedProperty().bind(btnUsers.visibleProperty());

        btnLoans.setVisible(role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("librarian"));
        btnLoans.managedProperty().bind(btnLoans.visibleProperty());
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

    @FXML
    private void handleShowAbout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/About.fxml"), resources);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(resources.getString("about.title"))
            ;
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenProfile() {
        try {
            URL fxmlLocation = getClass().getResource("/com/example/client/EditProfile.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlLocation, resources);
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setUserData(SessionManager.getLoggedInUser());

            Stage stage = new Stage();
            stage.setTitle(resources.getString("profile.edit.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            setupUserInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}