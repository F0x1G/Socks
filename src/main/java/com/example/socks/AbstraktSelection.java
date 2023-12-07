package com.example.socks;

import java.awt.*;

public class AbstraktSelection {

    public static photo ReplaceColor(photo image){
        int n = image.getN();
        int m = image.getM();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Color thiscolor = image.getPixel(i,j);
                thiscolor = findClosestColor(thiscolor);
                image.setPixel(i,j,thiscolor);
            }
        }
        return image;
    }
    public static Color findClosestColor(Color targetColor) {
        Color[] colorScheme = getColorScheme();
        Color closestColor = null;
        double minDistance = Double.MAX_VALUE;

        for (Color color : colorScheme) {
            double distance = calculateColorDistance(targetColor, color);
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color;
            }
        }

        return closestColor;
    }

    // Метод для обчислення відстані між двома кольорами в просторі RGB
    public static double calculateColorDistance(Color color1, Color color2) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        // Використовуйте відстань Евкліда для обчислення відстані між кольорами
        return Math.sqrt(Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2));
    }

    // Приклад кольорової схеми
    public static Color[] getColorScheme() {
        return new Color[]{
                new Color(255, 127, 127),
                new Color(255, 0, 0),
                new Color(127, 0, 0),
                new Color(50,30,0),
                new Color(100,60,0),
                new Color(255,160,0),
                new Color(255,255,0),
                new Color(255,255,127),
                new Color(127, 255, 127),
                new Color(0, 255, 0),
                new Color(0, 127, 0),
                new Color(0, 127, 127),
                new Color(0, 255, 255),
                new Color(127, 127, 255),
                new Color(0, 0, 255),
                new Color(0, 0, 127),
                new Color(138,43,226),
                new Color(255,100,226),
                new Color(255, 160, 200),
                new Color(255,255,255),
                new Color(127, 127, 127),
                new Color(0, 0, 0)
        };
    }
}
