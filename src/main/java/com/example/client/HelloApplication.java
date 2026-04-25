package com.example.client;

import com.example.client.util.LanguageManager;
import com.example.client.util.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        View startView = View.LOGIN;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(startView.getFxmlPath()));
        fxmlLoader.setResources(LanguageManager.getBundle());
        Scene scene = new Scene(fxmlLoader.load(), startView.getWidth(), startView.getHeight());
        stage.setTitle("Klient Knihovny");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}