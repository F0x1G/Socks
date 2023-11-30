package com.example.socks;

import java.awt.image.BufferedImage;


public class photo {

    public photo(int n, int m){
        this.n = n;
        this.m = m;
        Red = new int[n][m];
        Green = new int[n][m];
        Blue = new int[n][m];
    }

    public photo Photo(BufferedImage image, int n, int m){
        {
                photo phot = new photo(n,m);

                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        int rgb = image.getRGB(i, j);

                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        phot.Red[i][j] = red;
                        phot.Green[i][j] = green;
                        phot.Blue[i][j] = blue;
                    }
                }
                return phot;
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getRed(int n, int m) {
        return Red[n][m];
    }
    public int getGreen(int n, int m) {
        return Green[n][m];
    }
    public int getBlue(int n, int m) {
        return Blue[n][m];
    }


    private int [][] Red;
    private int [][] Green;
    private int [][] Blue;
    private int n;
    private int m;
}

