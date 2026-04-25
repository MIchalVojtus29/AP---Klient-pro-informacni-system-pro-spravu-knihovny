package com.example.client.controller;

import com.example.client.dto.*;
import com.example.client.service.*;
import com.example.client.util.AsyncManager;
import com.example.client.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AddLoanController {

    @FXML private ComboBox<BookResponseDto> bookCombo;
    @FXML private ComboBox<UserResponseDto> readerCombo;
    @FXML private DatePicker returnDatePicker;
    @FXML private ResourceBundle resources;

    private final BookService bookService = new BookService();
    private final UserService userService = new UserService();
    private final LoanService loanService = new LoanService();

    @FXML
    public void initialize() {
        setupComboBoxes();
        loadData();
    }

    private void setupComboBoxes() {
        bookCombo.setConverter(new StringConverter<>() {
            @Override public String toString(BookResponseDto b) { return b == null ? "" : b.getTitle(); }
            @Override public BookResponseDto fromString(String s) { return null; }
        });

        readerCombo.setConverter(new StringConverter<>() {
            @Override public String toString(UserResponseDto u) { return u == null ? "" : u.getFirstName() + " " + u.getLastName(); }
            @Override public UserResponseDto fromString(String s) { return null; }
        });
    }

    private void loadData() {
        AsyncManager.runAsync(() -> {
            try {
                List<BookResponseDto> books = bookService.fetchAllBooks(0, 100);
                List<UserResponseDto> users = userService.getUsers(0, 100, "").getContent();

                return new Object[]{books, users};
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, this::handleLoadSuccess, this::handleError);
    }

    private void handleLoadSuccess(Object result) {
        Object[] data = (Object[]) result;
        bookCombo.getItems().setAll((List<BookResponseDto>) data[0]);
        readerCombo.getItems().setAll((List<UserResponseDto>) data[1]);
    }

    @FXML
    private void handleSave() {
        BookResponseDto selectedBook = bookCombo.getValue();
        UserResponseDto selectedReader = readerCombo.getValue();
        LocalDate selectedDate = returnDatePicker.getValue();

        if (selectedBook == null || selectedReader == null || selectedDate == null) {
            showWarning(resources.getString("warning.noSelection"), resources.getString("loans.warning.fillAll"));
            return;
        }

        LoanCreateDto dto = new LoanCreateDto();
        dto.setBookId(selectedBook.getId().intValue());
        dto.setReaderId(selectedReader.getId().intValue());
        if (SessionManager.getLoggedInUser() != null) {
            dto.setLibrarianId(SessionManager.getLoggedInUser().getId().intValue());
        } else {
            dto.setLibrarianId(1);
        }
        dto.setReturnDate(returnDatePicker.getValue());

        AsyncManager.runAsync(
                () -> performCreateTask(dto),
                res -> handleSaveSuccess(null),
                this::handleError
        );
    }

    private Void performCreateTask(LoanCreateDto dto) {
        try {
            loanService.createLoan(dto);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleSaveSuccess(Void result) {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) bookCombo.getScene().getWindow()).close();
    }

    private void handleSaveSuccess() {
        ((Stage) bookCombo.getScene().getWindow()).close();
    }

    @FXML private void handleCancel() { ((Stage)bookCombo.getScene().getWindow()).close(); }

    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(resources.getString("warning.noSelection"));
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleError(Throwable err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText("Akce se nezdařila");
        alert.setContentText(err.getMessage());
        alert.showAndWait();
    }
}