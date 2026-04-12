package com.example.apknihovnaklient.util;

import javafx.application.Platform;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for handling background API calls in JavaFX.
 * Ensures that long-running tasks do not freeze the UI and results
 * are safely returned to the JavaFX Application Thread.
 */
public class AsyncManager {

    public static <T> void runAsync(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError) {

        CompletableFuture.supplyAsync(backgroundTask)
                .thenAccept(result -> {
                    Platform.runLater(() -> onSuccess.accept(result));
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        onError.accept(cause);
                    });
                    return null;
                });
    }
}