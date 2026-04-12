package com.example.apknihovnaklient.controller;

import com.example.apknihovnaklient.controller.api.BookApiClient;
import com.example.apknihovnaklient.dto.BookResponseDto;
import com.example.apknihovnaklient.dto.UserResponseDto;
import com.example.apknihovnaklient.util.AsyncManager;
import com.example.apknihovnaklient.util.LanguageManager;
import com.example.apknihovnaklient.util.UserSession;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for the main application window.
 * Manages the top bar, sidebar navigation, and book table.
 */
public class MainController {

    @FXML
    private Label userNameLabel;
    @FXML
    private Button btnLang;
    @FXML
    private TableView<BookResponseDto> bookTable;
    @FXML
    private TableColumn<BookResponseDto, Integer> colId;
    @FXML
    private TableColumn<BookResponseDto, String> colTitle;
    @FXML
    private TableColumn<BookResponseDto, String> colAuthor;
    @FXML
    private TableColumn<BookResponseDto, String> colGenre;
    @FXML
    private TableColumn<BookResponseDto, Integer> colYear;
    @FXML
    private TableColumn<BookResponseDto, Integer> colQuantity;

    @FXML
    private ResourceBundle resources;

    private final BookApiClient bookApiClient = new BookApiClient();

    @FXML
    public void initialize() {
        UserResponseDto user = UserSession.getLoggedUser();
        if (user != null) {
            userNameLabel.setText(user.getFirstName() + " " + user.getLastName() + " (" + user.getRole() + ")");
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genreName"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadBooksFromServer();
    }

    private void loadBooksFromServer() {
        AsyncManager.runAsync(
                () -> {
                    try {
                        return bookApiClient.getAllBooks();
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                },
                books -> {
                    bookTable.setItems(FXCollections.observableArrayList(books));
                },
                error -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not load books");
                    alert.setContentText(error.getMessage());
                    alert.showAndWait();
                }
        );
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        UserSession.clear();
        switchScene("/com/example/apknihovnaklient/Login.fxml", 350, 400);
    }

    @FXML
    public void handleLanguageSwitch(ActionEvent event) {
        if (LanguageManager.getCurrentLocale().getLanguage().equals("cs")) {
            LanguageManager.setLocale(new Locale("en", "US"));
        } else {
            LanguageManager.setLocale(new Locale("cs", "CZ"));
        }
        try {
            switchScene("/com/example/apknihovnaklient/Main.fxml", 900, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchScene(String fxmlPath, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setResources(LanguageManager.getBundle());
        Scene scene = new Scene(loader.load(), width, height);
        Stage stage = (Stage) userNameLabel.getScene().getWindow();
        stage.setScene(scene);
    }
}