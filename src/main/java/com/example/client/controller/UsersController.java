package com.example.client.controller;

import com.example.client.dto.PageResponse;
import com.example.client.dto.UserResponseDto;
import com.example.client.service.UserService;
import com.example.client.util.AsyncManager;
import com.example.client.util.LanguageManager;
import com.example.client.util.View;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UsersController {

    @FXML private TableView<UserResponseDto> userTable;
    @FXML private TableColumn<UserResponseDto, Long> colId;
    @FXML private TableColumn<UserResponseDto, String> colFirstName, colLastName, colEmail, colPhoneNumber, colRole;
    @FXML private TextField searchField;
    @FXML private Label pageLabel;
    @FXML private Button btnPrev, btnNext;

    private int currentPage = 0;
    private final int PAGE_SIZE = 20;
    private int totalPages = 1;

    private final UserService userService = new UserService();
    private javafx.collections.ObservableList<UserResponseDto> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();

        javafx.collections.transformation.FilteredList<UserResponseDto> filteredData = new javafx.collections.transformation.FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (user.getLastName() != null && user.getLastName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;

                return false;
            });
        });
        userTable.setItems(filteredData);

        loadUsersFromServer();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadUsersFromServer() {
        AsyncManager.runAsync(
                this::fetchCurrentUsers,
                this::handleUsersLoaded,
                this::handleUsersError
        );
    }

    private PageResponse<UserResponseDto> fetchCurrentUsers() {
        return fetchUsersTask(currentPage, "");
    }

    private void handleUsersLoaded(PageResponse<UserResponseDto> response) {
        masterData.setAll(response.getContent());
        totalPages = response.getTotalPages() == 0 ? 1 : response.getTotalPages();
        pageLabel.setText((currentPage + 1) + " / " + totalPages);
        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(currentPage >= totalPages - 1);
    }

    private void handleUsersError(Throwable err) {
        System.err.println("Error loading users:" + err.getMessage());
    }

    private PageResponse<UserResponseDto> fetchUsersTask(int page, String search) {
        try {
            return userService.getUsers(page, PAGE_SIZE, search);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadUsersFromServer();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadUsersFromServer();
        }
    }
    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.ADD_USER.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());

            Stage stage = new Stage();
            stage.setTitle(LanguageManager.getBundle().getString("user.add.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load(), View.ADD_USER.getWidth(), View.ADD_USER.getHeight()));

            stage.showAndWait();

            loadUsersFromServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditUser() {
        UserResponseDto selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nothing selected");
            alert.setContentText("Please select a user to edit.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.EDIT_USER.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());
            javafx.scene.Parent root = loader.load();

            EditUserController controller = loader.getController();
            controller.setUserData(selectedUser);

            Stage stage = new Stage();
            stage.setTitle(LanguageManager.getBundle().getString("user.edit.title"));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, View.EDIT_USER.getWidth(), View.EDIT_USER.getHeight()));

            stage.showAndWait();
            loadUsersFromServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser() {
        UserResponseDto selected = userTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning("Nothing selected", "Please select a user to delete.");
            return;
        }

        if (confirmDeletion(selected)) {
            AsyncManager.runAsync(
                    () -> performDeleteTask(selected.getId()),
                    this::handleDeleteSuccess,
                    this::handleDeleteError
            );
        }
    }

    private Void performDeleteTask(Integer id) {
        try {
            userService.deleteUser(id);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private void handleDeleteSuccess(Void result) {
        loadUsersFromServer();
    }
    private void handleDeleteError(Throwable err) {
        showErrorAlert("Chyba při mazání", err.getMessage());
    }
    private boolean confirmDeletion(UserResponseDto user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete user");
        alert.setHeaderText("Are you sure you want to delete the user " + user.getFirstName() + " " + user.getLastName() + "?");
        alert.setContentText("This action is irreversible.");
        return alert.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
    }

    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}