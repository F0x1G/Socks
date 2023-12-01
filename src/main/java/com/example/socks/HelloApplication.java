package com.example.socks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
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
        String inputPath = "445.bmp";
        String outPath = "out.bmp";
        BufferedImage image = ImageIO.read(new File(inputPath));
        int n = 200;
        int m = 200;
        BufferedImage image1 = PhotoEdit.resize(image, n, m);

        photo phot = photo.fromBufferedImage(image1);
        BufferedImage phottobuf = phot.toBufferedImage();
        PhotoEdit.saveImage(phottobuf, outPath);

        launch();
    }
}