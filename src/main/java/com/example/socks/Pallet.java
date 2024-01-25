package com.example.socks;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Pallet extends Application {

    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 300;

    private Image originalImage; // Original image
    private Image modifiedImage; // Image with color changes

    // RGB values for 5 main colors and background color
    private int[] colors = new int[6];

    private AnotherClass anotherClassInstance; // Pass an instance of the class containing setcolorSheme and getColorScheme

    public Pallet(AnotherClass anotherClassInstance) {
        this.anotherClassInstance = anotherClassInstance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Load an example image
        originalImage = new Image("example.jpg"); // Replace "example.jpg" with your image file

        // Create main window
        ImageView imageView = new ImageView(originalImage);
        Button openPalletButton = new Button("Open Pallet");
        openPalletButton.setOnAction(e -> openPalletWindow());

        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(imageView, openPalletButton);

        Scene mainScene = new Scene(mainLayout);
        primaryStage.setTitle("Color Pallet");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void openPalletWindow() {
        // Create color adjustment window
        Stage palletStage = new Stage();
        palletStage.initModality(Modality.APPLICATION_MODAL);
        palletStage.setTitle("Color Adjustment");

        VBox palletLayout = new VBox(10);

        for (int i = 0; i < 6; i++) {
            HBox colorBox = createColorBox(i);
            palletLayout.getChildren().add(colorBox);
        }

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            applyColorChanges();
            palletStage.close();
        });

        palletLayout.getChildren().add(okButton);

        Scene palletScene = new Scene(palletLayout);
        palletStage.setScene(palletScene);
        palletStage.show();
    }

    private HBox createColorBox(int index) {
        Label label = new Label("Color " + (index + 1) + ":");
        Slider slider = new Slider(0, 255, 0);
        TextField textField = new TextField("0");

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            textField.setText(String.valueOf(newValue.intValue()));
            colors[index] = newValue.intValue();
        });

        textField.setOnAction(e -> {
            try {
                int value = Integer.parseInt(textField.getText());
                if (value >= 0 && value <= 255) {
                    slider.setValue(value);
                    colors[index] = value;
                } else {
                    textField.setText(String.valueOf(slider.getValue()));
                }
            } catch (NumberFormatException ex) {
                textField.setText(String.valueOf(slider.getValue()));
            }
        });

        HBox colorBox = new HBox(10);
        colorBox.getChildren().addAll(label, slider, textField);

        return colorBox;
    }

    private void applyColorChanges() {
        // Get color scheme from AnotherClass
        Color[] col = HelloController.getColorScheme();

        // Apply color changes to the image
        // You need to implement your own logic to modify the image with the given RGB values
        // Here, we are just displaying the RGB values for demonstration purposes
        for (int i = 0; i < colors.length; i++) {
            System.out.println("Color " + (i + 1) + ": " + colors[i]);
        }

        // Update the modified image (replace this with your logic)
        modifiedImage = originalImage;

        // Update the main window with the modified image
        // (replace this with your logic to update the image in the main window)
    }
}

