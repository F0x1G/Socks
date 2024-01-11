package com.example.socks;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    @FXML
    private TextField LabelM;
    @FXML
    private TextField LabelN;
    @FXML
    private void onStartClick(ActionEvent event){

    }
    @FXML
    private  void onRes30Click(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int m = Integer.parseInt(LabelM.getText());
            int n = Integer.parseInt(LabelN.getText());
            double con =1.3;
            m= (int) (m*con);
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
    }
    @FXML
    private  void onRes60Click(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int m = Integer.parseInt(LabelM.getText());
            int n = Integer.parseInt(LabelN.getText());
            double con =1.6;
            m= (int) (m*con);
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
    }
    @FXML
    private void onResClick(ActionEvent event){
        Image image = imageView.getImage();
        BufferedImage img = SwingFXUtils.fromFXImage(image,null);
        try {
            int n = Integer.parseInt(LabelM.getText());
            int m = Integer.parseInt(LabelN.getText());
            img = PhotoEdit.resize(img,n,m);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        Image newImage = SwingFXUtils.toFXImage(img,null);
        imageView.setImage(newImage);

        int width = (int) imageView.getImage().getWidth();
        int height = (int) imageView.getImage().getHeight();

        LabelM.setText(String.valueOf(width));
        LabelN.setText(String.valueOf(height));
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
                    LabelM.setText(String.valueOf(width));
                    LabelN.setText(String.valueOf(height));
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