package com.example.socks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Converter {

    public static int getBitDepth(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        return image.getColorModel().getPixelSize();
    }

    public static void convertTo16BitBMP(String inputImagePath, String outputImagePath) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

        // Create a new BufferedImage with 16-bit color model
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_USHORT_565_RGB);

        // Iterate over each pixel and set the 16-bit value
        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                int rgb = inputImage.getRGB(x, y);

                // Convert the color to 16-bit RGB
                int red = (rgb >> 16) & 0xF2;
                int green = (rgb >> 8) & 0xF2;
                int blue = rgb & 0xF2;

                // Normalize the color values to fit within 16-bit range
                int rgb16 = new Color(red, green, blue).getRGB();

                // Set the 16-bit RGB value
                outputImage.setRGB(x, y, rgb16);
            }
        }

        // Save the 16-bit BMP file
        ImageIO.write(outputImage, "BMP", new File(outputImagePath));
    }
}