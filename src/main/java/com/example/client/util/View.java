package com.example.client.util;

/**
 * Central configuration of all application screens.
 * Eliminates magic constants and unifies the definition of resolution.
 */
public enum View {
    LOGIN("/com/example/client/Login.fxml", 350, 400),
    MAIN("/com/example/client/Main.fxml",1200,800),
    ADD_BOOK("/com/example/client/AddBook.fxml", 500, 450),
    EDIT_BOOK("/com/example/client/EditBook.fxml", 500, 450),
    ADD_AUTHOR("/com/example/client/AddAuthor.fxml", 400, 300),
    ADD_GENRE("/com/example/client/AddGenre.fxml", 400, 200),
    ADD_USER("/com/example/client/AddUser.fxml", 450, 450),
    EDIT_USER("/com/example/client/EditUser.fxml", 450, 450),
    ADD_LOAN("/com/example/client/AddLoan.fxml", 450, 400),
    BOOK_LIST("/com/example/client/BookList.fxml"),
    LOAN_LIST("/com/example/client/LoanList.fxml"),
    USER_LIST("/com/example/client/UserList.fxml");

    private final String fxmlPath;
    private final double width;
    private final double height;

    View(String fxmlPath, double width, double height) {
        this.fxmlPath = fxmlPath;
        this.width = width;
        this.height = height;
    }

    View(String fxmlPath) {
        this(fxmlPath, 0, 0);
    }


    public String getFxmlPath() {
        return fxmlPath;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}