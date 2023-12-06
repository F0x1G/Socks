package com.example.socks;

import java.awt.*;
import java.awt.image.BufferedImage;


public class photo {
    private Color[][] pixels;
    private int n; // висота
    private int m; // ширина

    // Конструктор
    public photo(int n, int m) {
        this.n = n;
        this.m = m;
        this.pixels = new Color[n][m];
    }

    // Метод get і set для Pixels
    public Color getPixel(int i, int j) {
        return pixels[i][j];
    }

    public Color[][] getPixelMatrix() {
        return pixels;
    }

    public void setPixel(int i, int j, Color value) {
        pixels[i][j] = value;
    }

    // Методи get і set для Red
    public int getRed(int i, int j) {
        return pixels[i][j].getRed();
    }

    public int[][] getRedMatrix() {
        int[][] redMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                redMatrix[i][j] = pixels[i][j].getRed();
            }
        }
        return redMatrix;
    }

    public void setRed(int i, int j, int value) {
        Color currentColor = pixels[i][j];
        pixels[i][j] = new Color(value, currentColor.getGreen(), currentColor.getBlue());
    }

    // Методи get і set для Green
    public int getGreen(int i, int j) {
        return pixels[i][j].getGreen();
    }

    public int[][] getGreenMatrix() {
        int[][] greenMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                greenMatrix[i][j] = pixels[i][j].getGreen();
            }
        }
        return greenMatrix;
    }

    public void setGreen(int i, int j, int value) {
        Color currentColor = pixels[i][j];
        pixels[i][j] = new Color(currentColor.getRed(), value, currentColor.getBlue());
    }

    // Методи get і set для Blue
    public int getBlue(int i, int j) {
        return pixels[i][j].getBlue();
    }

    public int[][] getBlueMatrix() {
        int[][] blueMatrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                blueMatrix[i][j] = pixels[i][j].getBlue();
            }
        }
        return blueMatrix;
    }

    public void setBlue(int i, int j, int value) {
        Color currentColor = pixels[i][j];
        pixels[i][j] = new Color(currentColor.getRed(), currentColor.getGreen(), value);
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    // Метод для конвертації з BufferedImage в Photo
    public static photo fromBufferedImage(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        photo photo = new photo(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);
                photo.setPixel(i, j, new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
            }
        }

        return photo;
    }

    // Метод для конвертації з Photo в BufferedImage
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(m, n, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Color pixel = getPixel(i, j);
                int rgb = pixel.getRGB();
                image.setRGB(j, i, rgb);
            }
        }

        return image;
    }
}

