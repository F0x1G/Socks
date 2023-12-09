package com.example.socks;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AbstraktSelection {

    public static photo EasySimplifier(photo image){
        int[][] indexmatrix = convertToColorIndices(image);
        int[][] cout = createElementCountMatrix(indexmatrix);
        double[] percentages = PercentageVisualization(cout);
        int repid = notOptimal(percentages);
        for(int i=0;i<repid;i++){
            cout = createElementCountMatrix(indexmatrix);
            int[] mincout =  rowWithMinSecondElement(cout);
            indexmatrix = ReplaceThis(mincout,cout,indexmatrix);
        }
        photo res = fromIntMatrix(indexmatrix);

        return res;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public static int[][] createElementCountMatrix(int[][] inputMatrix) {
    if (inputMatrix == null || inputMatrix.length == 0 || inputMatrix[0].length == 0) {
        throw new IllegalArgumentException("Invalid input matrix");
    }

    Map<Integer, Integer> elementCountMap = new HashMap<>();

    // Рахуємо кількість повторень для кожного елемента в матриці
    for (int[] row : inputMatrix) {
        for (int element : row) {
            elementCountMap.put(element, elementCountMap.getOrDefault(element, 0) + 1);
        }
    }

    // Створюємо нову матрицю з двома колонками: ім'я елемента і кількість його повторень
    int numRows = elementCountMap.size();
    int[][] resultMatrix = new int[numRows][2];

    int row = 0;
    for (Map.Entry<Integer, Integer> entry : elementCountMap.entrySet()) {
        resultMatrix[row][0] = entry.getKey();      // Ім'я елемента
        resultMatrix[row][1] = entry.getValue();    // Кількість повторень
        row++;
    }



    return resultMatrix;
}

    public static int[][] ReplaceThis(int[] target, int[][] list, int[][] pixMap) {
        try {
            int right = target[0] - 1;
            int left = target[0] + 1;
            int rElem = list[right][0];
            int lElem = list[left][0];
            int t = target[0];
            int tElem = list[t][0];
            int[][] newPixMap = pixMap;
            int dist1 = Math.abs(list[t][0] - list[left][0]);
            int dist2 = Math.abs(list[t][0] - list[right][0]);
            if (dist1 < dist2) {
                newPixMap = replaceValue(pixMap, tElem, lElem);
            } else if (dist2 < dist1) {
                newPixMap = replaceValue(pixMap, tElem, rElem);
            } else {
                if (list[left][2] < list[right][2]) {
                    newPixMap = replaceValue(pixMap, tElem, lElem);
                } else {
                    newPixMap = replaceValue(pixMap, tElem, rElem);
                }
            }
            return newPixMap;
        }catch (Exception e){
            int l = list.length;
            int right = target[0] - 1;
            int left = target[0] + 1;
            int t = target[0];
            int tElem = list[t][0];
            int[][] newPixMap = pixMap;

            if(left>l-1){
                int rElem = list[right][0];
                newPixMap = replaceValue(pixMap, tElem, rElem);
            }else {
                int lElem = list[left][0];
                newPixMap = replaceValue(pixMap, tElem, lElem);
            }
            return newPixMap;
        }
    }

    public static int notOptimal(double[] array) {
        int count = 0;
        // Рахуємо кількість елементів менше 10
        for (double value : array) {
            if (value < 2) {
                count++;
            }
        }return count;
    }

    public static int[][] replaceValue(int[][] matrix, int oldValue, int newValue) {
        // Проходження по кожному елементу матриці
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                // Перевірка, чи поточний елемент дорівнює oldValue
                if (matrix[i][j] == oldValue) {
                    // Заміна значення на newValue
                    matrix[i][j] = newValue;
                }
            }
        }

        // Повертаємо змінену матрицю
        return matrix;
    }

    public static int[][] sortMatrixDescending(int[][] matrix) {
        // Копія матриці для уникнення змін у вихідній матриці
        int[][] sortedMatrix = Arrays.copyOf(matrix, matrix.length);

        // Використовуємо алгоритм сортування бульбашкою
        int rows = sortedMatrix.length;
        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < rows - i - 1; j++) {
                // Порівнюємо значення у першому стовпці для сусідніх рядків
                if (sortedMatrix[j][0] < sortedMatrix[j + 1][0]) {
                    // Міняємо місцями рядки, якщо потрібно
                    int[] temp = sortedMatrix[j];
                    sortedMatrix[j] = sortedMatrix[j + 1];
                    sortedMatrix[j + 1] = temp;
                }
            }
        }
        // Повертаємо посортовану матрицю
        return sortedMatrix;
    }

    public static int[] rowWithMinSecondElement(int[][] matrix) {
        // Перевірка на null для матриці
        if (matrix == null || matrix.length == 0 || matrix[0].length != 2) {
            System.out.println("Некоректна матриця.");
            return null;
        }

        // Ініціалізація змінних для збереження рядка з найменшим другим елементом та його індексу
        int[] minRow = null;
        int minSecondElement = Integer.MAX_VALUE;
        int minRowIndex = -1;

        // Проходження по кожному рядку матриці
        for (int i = 0; i < matrix.length; i++) {
            int[] row = matrix[i];
            // Перевірка другого елемента у поточному рядку
            if (row[1] < minSecondElement) {
                minSecondElement = row[1];
                minRow = row;
                minRowIndex = i;
            }
        }

        // Повертаємо рядок з найменшим другим елементом та його індекс
        return new int[]{minRowIndex, minRow[0], minRow[1]};
    }


    public static double[] PercentageVisualization(int[][] matrix) {
        int targetColumn = 1;
        int totalSum = 0;
        int columnSum = 0;

        // Знайдемо суму елементів в цільовому стовпці та загальну суму матриці
        for (int[] row : matrix) {
            totalSum += row[targetColumn];
        }

        // Розрахунок процентного значення для кожного елемента
        double[] percentages = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            columnSum += matrix[i][targetColumn];
            percentages[i] = (double) matrix[i][targetColumn] / totalSum * 100;
        }

        // Повертаємо масив процентних значень
        return percentages;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static photo fromIntMatrix(int[][] intMatrix) {
        Color[] colorPalette = getColorScheme();
        int height = intMatrix.length;
        int width = (height > 0) ? intMatrix[0].length : 0;
        photo photo = new photo(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int colorIndex = intMatrix[i][j];
                if (colorIndex >= 0 && colorIndex < colorPalette.length) {
                    photo.setPixel(i, j, colorPalette[colorIndex]);
                } else {
                    // Обробка випадку, коли індекс виходить за межі палітри
                    // Здесь можна встановити якийсь стандартний колір або інші дії за замовчуванням
                    photo.setPixel(i, j, Color.BLACK);
                }
            }
        }
        return photo;
    }
    public static int[][] convertToColorIndices(photo photoObject) {
        int n = photoObject.getN();
        int m = photoObject.getM();
        int[][] colorIndices = new int[n][m];

        Color[] colorPalette = getColorScheme();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Color pixelColor = photoObject.getPixel(i, j);
                int colorIndex = findColorIndex(pixelColor, colorPalette);
                colorIndices[i][j] = colorIndex;
            }
        }

        return colorIndices;
    }

    private static int findColorIndex(Color targetColor, Color[] colorPalette) {
        for (int i = 0; i < colorPalette.length; i++) {
            if (targetColor.equals(colorPalette[i])) {
                return i;
            }
        }
        return -1; // якщо кольор не знайдено в палітрі

    }
    public static Color getColorFromIndex(int colorIndex, Color[] colorPalette) {
        if (colorIndex >= 0 && colorIndex < colorPalette.length) {
            return colorPalette[colorIndex];
        } else {
            throw new IllegalArgumentException("Invalid color index");
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    // Приклад кольорової схеми
    public static Color[] getColorScheme() {
        return new Color[]{
                new Color(255, 127, 127), // 0
                new Color(255, 0, 0), // 1
                new Color(127, 0, 0), // 2
                new Color(50,30,0), // 3
                new Color(100,60,0), // 4
                new Color(255,160,0), // 5
                new Color(255,255,0), // 6
                new Color(255,255,127), // 7
                new Color(127, 255, 127), // 8
                new Color(0, 255, 0), // 9
                new Color(0, 127, 0), // 10
                new Color(0, 127, 127), // 11
                new Color(0, 255, 255), // 12
                new Color(127, 127, 255), // 13
                new Color(0, 0, 255), // 14
                new Color(0, 0, 127), // 15
                new Color(138,43,226), // 16
                new Color(255,100,226), // 17
                new Color(255, 160, 200), // 18
                new Color(255,255,255), // 19
                new Color(127, 127, 127), // 20
                new Color(0, 0, 0) // 21
        };
    }
}
