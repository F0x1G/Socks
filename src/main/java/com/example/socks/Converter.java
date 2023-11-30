package com.example.socks;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Converter {

    public static void main(String[] args) {
        String inputImagePath = "white.png";
        String outputImagePath = "white250.bmp";

        try {
            int originalBitDepth = getBitDepth(inputImagePath);
            System.out.println("Original Bit Depth: " + originalBitDepth + " bits");

            convertTo16BitBMP(inputImagePath, outputImagePath);
            System.out.println("Conversion to 16-bit BMP completed successfully.");
        } catch (IOException e) {
            System.err.println("Error during conversion: " + e.getMessage());
        }
    }

    private static int getBitDepth(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        return image.getColorModel().getPixelSize();
    }

    private static void convertTo16BitBMP(String inputImagePath, String outputImagePath) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

        // Create a new BufferedImage with 16-bit color model
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_USHORT_565_RGB);

        // Iterate over each pixel and set the 16-bit value
        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                int rgb = inputImage.getRGB(x, y);

                // Convert the color to 16-bit RGB
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Normalize the color values to fit within 16-bit range
                int rgb16 = ((red & 0xF8) << 2) | ((green & 0xF8) << 5) | ((blue & 0xF8) << 0);

                // Set the 16-bit RGB value
                outputImage.setRGB(x, y, rgb16);
            }
        }

        // Save the 16-bit BMP file
        ImageIO.write(outputImage, "BMP", new File(outputImagePath));
    }
}