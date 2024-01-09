package com.example.socks;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.io.File;
import java.util.List;
import javax.swing.*;

public class HelloController {
    @FXML
    private Button Load;
    @FXML
    private Label fileNameLabel;
    @FXML
    private ImageView imageView;
    private PhotoLoader photoLoader = new PhotoLoader();
    @FXML
    private void handleButtonClick(ActionEvent event) {
        openFileChooser();
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(Load.getScene().getWindow());

        if (selectedFiles != null) {
            // Process the selected files
            for (File file : selectedFiles) {
                // Add your logic to handle the selected files
                System.out.println("Selected File: " + file.getAbsolutePath());

                // Load photo using PhotoLoader
                if (photoLoader.loadPhoto(file)) {
                    // Display the name of the loaded file
                    fileNameLabel.setText("Loaded File: " + file.getName());

                    // Display the image in the ImageView
                    imageView.setImage(new Image(file.toURI().toString()));

                    // Get and display image dimensions
                    int width = photoLoader.getImageWidth();
                    int height = photoLoader.getImageHeight();
                    System.out.println("Image Dimensions: " + width + "x" + height);
                } else {
                    // Handle photo loading error
                    System.out.println("Error loading photo.");
                }
            }
        } else {
            // Handle case where user canceled file selection
            System.out.println("File selection canceled.");
        }
    }
}