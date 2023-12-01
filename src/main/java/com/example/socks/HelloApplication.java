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
        String inputPath = "445.bmp";
        String outPath = "out.bmp";
        BufferedImage image = ImageIO.read(new File(inputPath));
        int n = 200;
        int m = 400;
        BufferedImage image1 = PhotoEdit.resize(image, n, m);

        photo phot = photo.fromBufferedImage(image1);
        Color c1 = new Color(255,255,255);
        Color c2 = new Color(0,255,128);
        PhotoEdit.bucket(phot, c1,c2);
        BufferedImage phottobuf = phot.toBufferedImage();
        PhotoEdit.saveImage(phottobuf, outPath);



        launch();
    }
}