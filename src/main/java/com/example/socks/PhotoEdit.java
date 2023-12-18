package com.example.socks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoEdit {

    public static BufferedImage resize(BufferedImage image, int n, int m) throws IOException {
        BufferedImage resizedImage = new BufferedImage(n, m, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(image, 0, 0, n, m, null);
        return resizedImage;
    }

    public static photo bucket(photo photo, Color color1, Color color2) {
        int targetRed = color1.getRed();
        int targetGreen = color1.getGreen();
        int targetBlue = color1.getBlue();

        int replacementRed = color2.getRed();
        int replacementGreen = color2.getGreen();
        int replacementBlue = color2.getBlue();

        int n = photo.getN();
        int m = photo.getM();

        int[][] red = photo.getRedMatrix();
        int[][] green = photo.getGreenMatrix();
        int[][] blue = photo.getBlueMatrix();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (red[i][j] == targetRed && green[i][j] == targetGreen && blue[i][j] == targetBlue) {
                    Color repleysColor = new Color(replacementRed,replacementGreen,replacementBlue);
                    photo.setPixel(i,j,repleysColor);
                }
            }
        }


        return photo;
    }


    public static void saveImage(BufferedImage image, String outputPath) throws IOException {
        ImageIO.write(image, "bmp", new File(outputPath));
    }

}
