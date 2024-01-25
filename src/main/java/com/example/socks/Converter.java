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

    public static void convertTo16BitBMP(BufferedImage inputImage, String outputImagePath) throws IOException {
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
        int[][] CordsColor = getIsExisten(Colors,0);
        Color[][] Schema = AbstraktSelection.StanokSheme();
        int[][] intimage = AbstraktSelection.convertToColorIndices(image);

        for(int j=0;j<CordsColor.length;j++) {
            int[] cord = CordsColor[j];
            int OLD = Colors[cord[0]][cord[1]];
            int k = 120000 + ((cord[0] * 10) + cord[1]);
            replaceValues(intimage, OLD, k);
        }

        int[][] coutint = getIsExisten(intimage,119999);

        for(int i=0;i<coutint.length;i++) {
            int[] cord = coutint[i];
            int cor = intimage[cord[0]][cord[1]];
            int x = (int) (cor-120000)/10;
            int y = (int) ((double)( (cor - 120000) /10)-x)*10;
            Color newColor = Schema[x][y];
            image.setPixel(cord[0],cord[1],newColor);
        }

        return image;
    }

    public static void replaceValues(int[][] matrix, int oldVal, int newVal) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == oldVal) {
                    matrix[i][j] = newVal;
                }
            }
        }
    }

    public static int[][] getIsExisten(int[][] matrix, int target) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        List<int[]> nonNegativeCoordinates = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (matrix[i][j] > target) {
                    nonNegativeCoordinates.add(new int[]{i, j});
                }
            }
        }
        int[][] resultMatrix = new int[nonNegativeCoordinates.size()][2];
        for (int i = 0; i < nonNegativeCoordinates.size(); i++) {
            resultMatrix[i] = nonNegativeCoordinates.get(i);
        }

        return resultMatrix;
    }

    public static BufferedImage PhotoDlaZak(BufferedImage photo) throws IOException {
        photo image = com.example.socks.photo.fromBufferedImage(photo);
        image = AbstraktSelection.ReplaceColor(image);
        int[][] colorsheme = AbstraktSelection.convertToColorIndices(image);
        int[][] newColorSheme = intFromZak(colorsheme);
        photo newImage=AbstraktSelection.fromIntMatrix(newColorSheme);
        BufferedImage Finish = newImage.toBufferedImage();
        return Finish;
    }

    public static int[][] intFromZak (int[][] Start) {
        int rows = 2+Start.length * 5;
        int cols = Start[0].length * 5;
        int[][] Finish = new int[rows][cols];
        for (int i = 0; i < Start.length; i++) {
            for (int j = 0; j < Start[0].length; j++) {
                int x = 2 + (5 * j);
                int y = 4 + (5 * i);

                Finish[y][x-2] = Start[i][j];
                Finish[y-1][x-2] = Start[i][j];
                Finish[y-2][x-2] = Start[i][j];
                Finish[y-3][x-2] = Start[i][j];
                Finish[y-4][x-2] = Start[i][j];

                Finish[y+1][x-1] = Start[i][j];
                Finish[y][x-1] = Start[i][j];
                Finish[y-1][x-1] = Start[i][j];
                Finish[y-2][x-1] = Start[i][j];
                Finish[y-3][x-1] = Start[i][j];

                Finish[y-2][x] = Start[i][j];
                Finish[y-1][x] = Start[i][j];
                Finish[y][x] = Start[i][j];
                Finish[y+1][x] = Start[i][j];
                Finish[y+2][x] = Start[i][j];

                Finish[y+1][x+1] = Start[i][j];
                Finish[y][x+1] = Start[i][j];
                Finish[y-1][x+1] = Start[i][j];
                Finish[y-2][x+1] = Start[i][j];
                Finish[y-3][x+1] = Start[i][j];

                Finish[y][x+2] = Start[i][j];
                Finish[y-1][x+2] = Start[i][j];
                Finish[y-2][x+2] = Start[i][j];
                Finish[y-3][x+2] = Start[i][j];
                Finish[y-4][x+2] = Start[i][j];
            }
        }
        return Finish;
    }

}