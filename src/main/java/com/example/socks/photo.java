package com.example.socks;

import java.awt.*;
import java.awt.image.BufferedImage;


public class photo {
    private int[][] red;
    private int[][] green;
    private int[][] blue;
    private int n; // висота
    private int m; // ширина

    // Конструктор
    public photo(int n, int m) {
        this.n = n;
        this.m = m;
        this.red = new int[n][m];
        this.green = new int[n][m];
        this.blue = new int[n][m];
    }

    // Методи get і set для Red
    public int getRed(int i, int j) {
        return red[i][j];
    }
    public int[][] getRedMatrix() {
        return red;
    }

    public void setRed(int i, int j, int value) {
        red[i][j] = value;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    // Методи get і set для Green
    public int getGreen(int i, int j) {
        return green[i][j];
    }
    public int[][] getGreenMatrix() {
        return green;
    }

    public void setGreen(int i, int j, int value) {
        green[i][j] = value;
    }

    // Методи get і set для Blue
    public int getBlue(int i, int j) {
        return blue[i][j];
    }

    public int[][] getBlueMatrix() {
        return blue;
    }

    public void setBlue(int i, int j, int value) {
        blue[i][j] = value;
    }

    // Метод для конвертації з BufferedImage в Photo
    public static photo fromBufferedImage(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        photo photo = new photo(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);
                photo.setRed(i, j, (rgb >> 16) & 0xFF);
                photo.setGreen(i, j, (rgb >> 8) & 0xFF);
                photo.setBlue(i, j, rgb & 0xFF);
            }
        }

        return photo;
    }

    // Метод для конвертації з Photo в BufferedImage
    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(m, n, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int rgb = new Color(getRed(i,j),getGreen(i,j),getBlue(i,j)).getRGB();
                image.setRGB(j, i, rgb);
            }
        }

        return image;
    }
}

