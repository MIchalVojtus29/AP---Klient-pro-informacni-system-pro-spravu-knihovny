package com.example.client.controller;

import com.example.client.dto.LoanResponseDto;
import com.example.client.dto.PageResponse;
import com.example.client.service.LoanService;
import com.example.client.util.AsyncManager;
import com.example.client.util.LanguageManager;
import com.example.client.util.View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class LoansController {

    @FXML private TableView<LoanResponseDto> loanTable;
    @FXML private TableColumn<LoanResponseDto, String> colBook, colReader, colLibrarian, colStatus;
    @FXML private TableColumn<LoanResponseDto, String> colLoanDate, colReturnDate;
    @FXML private TextField searchField;
    @FXML private Label pageLabel;
    @FXML private Button btnPrev, btnNext;
    @FXML private ResourceBundle resources;

    private int currentPage = 0;
    private final int PAGE_SIZE = 20;
    private int totalPages = 1;

    private final LoanService loanService = new LoanService();
    private final ObservableList<LoanResponseDto> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupFiltering();
        loadLoansFromServer();
    }

    private void setupTable() {
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colReader.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colLibrarian.setCellValueFactory(new PropertyValueFactory<>("librarianName"));
        colLoanDate.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        colReturnDate.setText(resources.getString("loans.dueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupFiltering() {
        FilteredList<LoanResponseDto> filteredData = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(loan -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String filter = newVal.toLowerCase();
                return loan.getBookTitle().toLowerCase().contains(filter) ||
                        loan.getReaderName().toLowerCase().contains(filter);
            });
        });
        loanTable.setItems(filteredData);
    }

    private void loadLoansFromServer() {
        AsyncManager.runAsync(
                this::fetchLoansTask,
                this::handleLoansLoaded,
                this::handleError
        );
    }

    private PageResponse<LoanResponseDto> fetchLoansTask() {
        try {
            return loanService.getLoans(currentPage, PAGE_SIZE);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleLoansLoaded(PageResponse<LoanResponseDto> response) {
        masterData.setAll(response.getContent());
        totalPages = response.getTotalPages() == 0 ? 1 : response.getTotalPages();
        pageLabel.setText((currentPage + 1) + " / " + totalPages);
        btnPrev.setDisable(currentPage == 0);
        btnNext.setDisable(currentPage >= totalPages - 1);
    }

    private void handleError(Throwable err) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Error: " + err.getMessage());
        alert.show();
    }

    @FXML private void handlePreviousPage() { if (currentPage > 0) { currentPage--; loadLoansFromServer(); } }
    @FXML private void handleNextPage() { if (currentPage < totalPages - 1) { currentPage++; loadLoansFromServer(); } }

    @FXML
    private void handleNewLoan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(View.ADD_LOAN.getFxmlPath()));

            loader.setResources(LanguageManager.getBundle());

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(resources.getString("loans.add.title"));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root, View.ADD_LOAN.getWidth(), View.ADD_LOAN.getHeight()));

            stage.showAndWait();

            loadLoansFromServer();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error opening window", e.getMessage());
        }
    }

    @FXML
    private void handleReturnBook() {
        LoanResponseDto selected = loanTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarning(resources.getString("warning.noSelection"), resources.getString("loans.warning.selectLoan"));
            return;
        }

        if ("returned".equals(selected.getStatus())) {
            return;
        }

        AsyncManager.runAsync(
                () -> performReturnTask(selected.getId()),
                this::handleReturnSuccess,
                this::handleError
        );
    }

    private Void performReturnTask(Long loanId) {
        try {
            loanService.returnBook(loanId);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleReturnSuccess(Void result) {
        loadLoansFromServer();
    }

    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(resources.getString("warning.noSelection"));
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