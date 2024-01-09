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
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        String inputImagePath = "445.jpg";
        String outputImagePath = "image.bmp";
        String outputImagePath1 = "out.bmp";
        String saveStanokVision ="StanokOut.bmp";

        String textureImagePath = "StitchTexture.jpg";
        String outputImagePathStitch = "outStitch.bmp";

        try {
            int originalBitDepth = Converter.getBitDepth(inputImagePath);

            System.out.println("Original Bit Depth: " + originalBitDepth + " bits");
            Converter.convertTo16BitBMP(inputImagePath, outputImagePath);//knopka pererobotku

            Converter.convertTo16BitBMP(inputImagePath, outputImagePath);

            BufferedImage img = ImageIO.read(new File(outputImagePath));//dlya zagryzku
            img = PhotoEdit.resize(img,200,200);//2 text field

            photo image = photo.fromBufferedImage(img);//kolu photo zagruzheno, pislya resize

            image = AbstraktSelection.main(image);//dlya sprochenya photo, pislya fromBuff, okrema knopka dlya sproshennya

            image = Stanok.main(image, true,1);//pislya Abstrakt, pislya vyboru rejima(pislya zagryzku)(3 flaga)

            photo image1 = photo.fromBufferedImage(img);//pislya stanka yaksho true, bez knopky

            image1.setPhoto(image);//pislya stanka yaksho true

            photo stanokImg = Converter.StanokVision(image);//pislya stanka yaksho true

            BufferedImage img2 = stanokImg.toBufferedImage();//
            PhotoEdit.saveImage(img2,saveStanokVision);//

            BufferedImage img1 = image1.toBufferedImage();//
            PhotoEdit.saveImage(img1,outputImagePath1);//

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