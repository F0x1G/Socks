package com.example.socks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        System.out.println("ura?");
    }

    public static void main(String[] args) throws IOException {
        String inputImagePath = "445.jpg";
        String outputImagePath = "image.bmp";

        try {
            int originalBitDepth = Converter.getBitDepth(inputImagePath);
            System.out.println("Original Bit Depth: " + originalBitDepth + " bits");

            Converter.convertTo16BitBMP(inputImagePath, outputImagePath);
            System.out.println("Conversion to 16-bit BMP completed successfully.");
        } catch (IOException e) {
            System.err.println("Error during conversion: " + e.getMessage());
        }
        launch();
    }
}