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
        String outputImagePath1 = "out.bmp";

        try {
            int originalBitDepth = Converter.getBitDepth(inputImagePath);
            System.out.println("Original Bit Depth: " + originalBitDepth + " bits");
            Converter.convertTo16BitBMP(inputImagePath, outputImagePath);

            BufferedImage img = ImageIO.read(new File(outputImagePath));
            img = PhotoEdit.resize(img,200,200);

            photo image = photo.fromBufferedImage(img);
            AbstraktSelection.ReplaceColor(image);
            BufferedImage img1 = image.toBufferedImage();
            PhotoEdit.saveImage(img1,outputImagePath1);

        } catch (IOException e) {
            System.err.println("Error during conversion: " + e.getMessage());
        }
        launch();
    }
}