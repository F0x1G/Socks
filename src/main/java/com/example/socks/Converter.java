package com.example.socks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static void applyStitchTexture(BufferedImage baseImage, BufferedImage textureImage) {
        int baseWidth = baseImage.getWidth();
        int baseHeight = baseImage.getHeight();

        int textureWidth = textureImage.getWidth();
        int textureHeight = textureImage.getHeight();

        for (int y = 0; y < baseHeight; y++) {
            for (int x = 0; x < baseWidth; x++) {
                // Використовуємо залишок від ділення для повторення текстури
                int textureX = x % textureWidth;
                int textureY = y % textureHeight;

                if (textureX < textureWidth && textureY < textureHeight) {
                    int baseColor = baseImage.getRGB(x, y);
                    int textureColor = textureImage.getRGB(textureX, textureY);

                    // перемноження кольорів
                    int newColor = multiplyColors(baseColor, textureColor);

                    baseImage.setRGB(x, y, newColor);
                } else {
                    // Встановлюємо білий колір для пікселів поза межами текстурного зображення
                    baseImage.setRGB(x, y, 0xFFFFFFFF);
                }
            }
        }
    }
    private static int multiplyColors(int baseColor, int textureColor) {
        int baseAlpha = (baseColor >> 24) & 0xFF;
        int baseRed = (baseColor >> 16) & 0xFF;
        int baseGreen = (baseColor >> 8) & 0xFF;
        int baseBlue = baseColor & 0xFF;

        int textureAlpha = (textureColor >> 24) & 0xFF;
        int textureRed = (textureColor >> 16) & 0xFF;
        int textureGreen = (textureColor >> 8) & 0xFF;
        int textureBlue = textureColor & 0xFF;

        int newAlpha = (baseAlpha * textureAlpha) / 255;
        int newRed = (baseRed * textureRed) / 255;
        int newGreen = (baseGreen * textureGreen) / 255;
        int newBlue = (baseBlue * textureBlue) / 255;

        return (newAlpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
    }

    public static photo StanokVision(photo image){
        int[][] Colors = image.getStanokScheme();
        Color[][] inStanok = AbstraktSelection.StanokSheme();
        int[][] CordsColor = getIsExisten(Colors);
        Color[] Schema = AbstraktSelection.getColorScheme();
        for(int i=0;i<CordsColor.length;i++){
            int[] cord = CordsColor[i];
            int k = Colors[cord[0]][cord[1]];
            Color color1 = Schema[k];
            Color color2 = inStanok[cord[0]][cord[1]];
            PhotoEdit.bucket(image, color1, color2);
        }
        return image;
    }

    public static int[][] getIsExisten(int[][] matrix) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        // Ліст для збереження координат елементів, які не є -1 або -2
        List<int[]> nonNegativeCoordinates = new ArrayList<>();

        // Знаходимо координати елементів, які не є -1 або -2
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (matrix[i][j] > 0) {
                    nonNegativeCoordinates.add(new int[]{i, j});
                }
            }
        }
        // Перетворюємо ліст в матрицю
        int[][] resultMatrix = new int[nonNegativeCoordinates.size()][2];
        for (int i = 0; i < nonNegativeCoordinates.size(); i++) {
            resultMatrix[i] = nonNegativeCoordinates.get(i);
        }

        return resultMatrix;
    }

}