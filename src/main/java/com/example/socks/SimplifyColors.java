package com.example.socks;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimplifyColors {

    public static BufferedImage simplifyColors(BufferedImage originalImage, int numberOfColors) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Отримуємо список кольорів зображення
        List<Color> allColors = getAllColors(originalImage);

        // Використовуємо k-means для знаходження представницьких кольорів
        List<Color> representativeColors = kMeansClustering(allColors, numberOfColors);

        // Створюємо нове зображення з представницькими кольорами
        BufferedImage simplifiedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(originalImage.getRGB(x, y));
                Color closestColor = findClosestColor(pixelColor, representativeColors);
                simplifiedImage.setRGB(x, y, closestColor.getRGB());
            }
        }

        return simplifiedImage;
    }

    private static List<Color> getAllColors(BufferedImage image) {
        List<Color> colors = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                colors.add(color);
            }
        }

        return colors;
    }

    private static List<Color> kMeansClustering(List<Color> colors, int k) {
        List<Color> centroids = new ArrayList<>();
        Random random = new Random();

        // Вибираємо початкові центроїди випадковим чином
        for (int i = 0; i < k; i++) {
            centroids.add(colors.get(random.nextInt(colors.size())));
        }

        int maxIterations = 100;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // Призначаємо кожен колір до найближчого центроїду
            List<List<Color>> clusters = new ArrayList<>(k);
            for (int i = 0; i < k; i++) {
                clusters.add(new ArrayList<>());
            }

            for (Color color : colors) {
                int closestCentroidIndex = findClosestCentroidIndex(color, centroids);
                clusters.get(closestCentroidIndex).add(color);
            }

            // Оновлюємо центроїди
            List<Color> newCentroids = new ArrayList<>();
            for (List<Color> cluster : clusters) {
                Color newCentroid = calculateAverageColor(cluster);
                newCentroids.add(newCentroid);
            }

            // Якщо центроїди не змінились, завершуємо процес
            if (centroids.equals(newCentroids)) {
                break;
            }

            centroids = newCentroids;
        }

        return centroids;
    }

    private static int findClosestCentroidIndex(Color color, List<Color> centroids) {
        int closestIndex = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            double distance = calculateColorDistance(color, centroids.get(i));

            if (distance < closestDistance) {
                closestIndex = i;
                closestDistance = distance;
            }
        }

        return closestIndex;
    }

    private static Color calculateAverageColor(List<Color> colors) {
        if (colors.isEmpty()) {
            return new Color(0, 0, 0); // Handle the case when the list is empty
        }
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;

        for (Color color : colors) {
            totalRed += color.getRed();
            totalGreen += color.getGreen();
            totalBlue += color.getBlue();
        }

        int averageRed = totalRed / colors.size();
        int averageGreen = totalGreen / colors.size();
        int averageBlue = totalBlue / colors.size();

        return new Color(averageRed, averageGreen, averageBlue);
    }

    private static double calculateColorDistance(Color color1, Color color2) {
        int redDiff = color1.getRed() - color2.getRed();
        int greenDiff = color1.getGreen() - color2.getGreen();
        int blueDiff = color1.getBlue() - color2.getBlue();

        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    private static Color findClosestColor(Color targetColor, List<Color> colors) {
        int closestIndex = 0;
        double closestDistance = Double.MAX_VALUE;

        for (int i = 0; i < colors.size(); i++) {
            double distance = calculateColorDistance(targetColor, colors.get(i));

            if (distance < closestDistance) {
                closestIndex = i;
                closestDistance = distance;
            }
        }

        return colors.get(closestIndex);
    }
}

