package com.example.apknihovnaklient;

import com.example.apknihovnaklient.util.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        fxmlLoader.setResources(LanguageManager.getBundle());
        Scene scene = new Scene(fxmlLoader.load(), 350, 400);
        stage.setTitle("Klient Knihovny");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}