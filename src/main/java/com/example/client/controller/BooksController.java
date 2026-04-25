package com.example.client.controller;

import com.example.client.dto.BookResponseDto;
import com.example.client.service.BookService;
import com.example.client.util.AsyncManager;
import com.example.client.util.LanguageManager;
import com.example.client.util.View;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Book List screen.
 * Handles the table, pagination, search, and CRUD operations for books.
 */
public class BooksController {

    @FXML private TableView<BookResponseDto> bookTable;
    @FXML private TableColumn<BookResponseDto, Long> colId;
    @FXML private TableColumn<BookResponseDto, String> colTitle;
    @FXML private TableColumn<BookResponseDto, String> colAuthor;
    @FXML private TableColumn<BookResponseDto, String> colGenre;
    @FXML private TableColumn<BookResponseDto, Integer> colYear;
    @FXML private TableColumn<BookResponseDto, Integer> colQuantity;
    @FXML private TextField searchField;
    @FXML private Label pageLabel;
    @FXML private Button btnPrev, btnNext;
    @FXML private ResourceBundle resources;

    private int currentPage = 0;
    private final int PAGE_SIZE = 20;
    private final BookService bookService;
    private javafx.collections.ObservableList<BookResponseDto> masterData = FXCollections.observableArrayList();

    public BooksController() {
        this.bookService = new BookService();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        javafx.collections.transformation.FilteredList<BookResponseDto> filteredData = new javafx.collections.transformation.FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (book.getAuthorName() != null && book.getAuthorName().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        bookTable.setItems(filteredData);

        loadBooksFromServer();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genreName"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void loadBooksFromServer() {
        AsyncManager.runAsync(
                this::performFetchBooksTask,
                this::handleLoadBooksSuccess,
                this::handleLoadBooksError
        );
    }

    private List<BookResponseDto> performFetchBooksTask() {
        try {
            return bookService.fetchAllBooks(currentPage, PAGE_SIZE);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleLoadBooksSuccess(List<BookResponseDto> books) {
        masterData.setAll(books);
        pageLabel.setText((currentPage + 1) + "");
        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(books.size() < PAGE_SIZE);
    }

    private void handleLoadBooksError(Throwable ex) {
        showErrorAlert(resources.getString("book.error.load"), ex.getMessage());
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadBooksFromServer();
        }
    }

    @FXML
    private void handleNextPage() {
        currentPage++;
        loadBooksFromServer();
    }

    @FXML
    private void handleAddBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.ADD_BOOK.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());

            Stage stage = new Stage();
            stage.setTitle(resources.getString("book.add.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load(), View.ADD_BOOK.getWidth(), View.ADD_BOOK.getHeight()));
            stage.showAndWait();

            loadBooksFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditBook() {
        BookResponseDto selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning(resources.getString("warning.noSelection"), resources.getString("book.error.edit.noSelection"));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.EDIT_BOOK.getFxmlPath()));
            loader.setResources(LanguageManager.getBundle());
            Parent root = loader.load();

            EditBookController controller = loader.getController();
            controller.setBookData(selected);

            Stage stage = new Stage();
            stage.setTitle(resources.getString("book.edit.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, View.EDIT_BOOK.getWidth(), View.EDIT_BOOK.getHeight()));
            stage.showAndWait();

            loadBooksFromServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteBook() {
        BookResponseDto selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showWarning(resources.getString("warning.noSelection"), resources.getString("book.error.delete.noSelection"));
            return;
        }

        if (confirmDeletion(selectedBook.getTitle())) {
            AsyncManager.runAsync(
                    () -> performDeleteTask(selectedBook.getId()),
                    this::handleDeleteSuccess,
                    this::handleDeleteError
            );
        }
    }

    private Void performDeleteTask(Long id) {
        try {
            bookService.removeBook(id);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleDeleteSuccess(Object result) {
        loadBooksFromServer();
    }

    private void handleDeleteError(Throwable ex) {
        showErrorAlert(resources.getString("error.delete.title"), ex.getMessage());
    }

    private boolean confirmDeletion(String bookTitle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resources.getString("book.delete.confirm.title"));
        alert.setHeaderText(resources.getString("book.delete.confirm.header"));
        alert.setContentText(resources.getString("book.delete.confirm.content") + " " + bookTitle + "?");

        return alert.showAndWait().filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }

    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resources.getString("error.title"));
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}