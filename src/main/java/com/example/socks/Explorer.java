package com.example.socks;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class Explorer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Assuming you have an existing JavaFX project, you can integrate this code into your existing codebase.

        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        // Show the file dialog
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        if (selectedFiles != null) {
            // Process the selected files
            for (File file : selectedFiles) {
                // Add your logic to handle the selected files
                System.out.println("Selected File: " + file.getAbsolutePath());
            }
        } else {
            // Handle case where user canceled file selection
            System.out.println("File selection canceled.");
        }
    }
}

