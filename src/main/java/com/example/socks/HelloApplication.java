package com.example.socks;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

        String textureImagePath = "StitchTexture.jpg";
        String outputImagePathStitch = "outStitch.bmp";

        try {
            int originalBitDepth = Converter.getBitDepth(inputImagePath);
            System.out.println("Original Bit Depth: " + originalBitDepth + " bits");
            Converter.convertTo16BitBMP(inputImagePath, outputImagePath);

            BufferedImage img = ImageIO.read(new File(outputImagePath));
            img = PhotoEdit.resize(img,200,200);

            photo image = photo.fromBufferedImage(img);

            image = AbstraktSelection.main(image);

            image = Stanok.main(image, true,3);
            AbstraktSelection.printMatrix(image.getStanokScheme());

            BufferedImage img1 = image.toBufferedImage();
            PhotoEdit.saveImage(img1,outputImagePath1);

            // Накладання текстури
            BufferedImage texture = ImageIO.read(new File(textureImagePath));
            Converter.applyStitchTexture(img, texture);

            // Збереження обробленої фотографії
            PhotoEdit.saveImage(img, outputImagePathStitch);


        } catch (IOException e) {
            System.err.println("Error during conversion: " + e.getMessage());
        }
        launch();
    }
}