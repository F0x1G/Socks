package com.example.socks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;

public class Stanok {

    public static int[][] ThisRejim(int rejim){
        int[][] Sta = new int[6][3];
        for (int i = 0; i < Sta.length; i++) {
            for (int j = 0; j < Sta[i].length; j++) {
                Sta[i][j] = -1;
            }
        }
        Sta[0][2]=-2;
        Sta[1][2]=-2;
        Sta[5][2]=-2;
        Sta[5][1]=-2;

        if (rejim==2){
            Sta[0][1]=-2;
            Sta[0][0]=-2;
        } else if (rejim==3) {
            Sta[0][1]=-2;
            Sta[0][0]=-2;

            Sta[1][1]=-2;
            Sta[1][0]=-2;
        }
        return Sta;
    }

    public static photo main(photo image,boolean optimathe,int rejim, int Q, double T, double S, double B) {
        int[][] Sta = ThisRejim(rejim);

        if(optimathe){
            image = PhotoOptimither(image, rejim, Q,T,S,B);
        }else {
            int[][] imageMat = AbstraktSelection.convertToColorIndices(image);
            int m = image.getM();
            int n = image.getN();
            imageMat = MatrixCheck(imageMat,Sta);
            imageMat = FindContrast(imageMat);
            image = AbstraktSelection.fromIntMatrix(imageMat);
        }

        return image;
    }


    public static int[][] FindContrast(int[][] imageMat){
        int[][] cout = AbstraktSelection.createElementCountMatrix(imageMat);
        Color[] Schema = AbstraktSelection.getColorScheme();
        Color[] thisSchema = new Color[cout.length];
        for(int i = 0;i< cout.length;i++){
                thisSchema[i] = Schema[cout[i][0]];
        }
        for(int j=0;j< thisSchema.length-1;j++) {
            thisSchema[j] = Schema[AbstraktSelection.findMostContrastingColorIndex(thisSchema[j])];
        }
        int[] ColorsIndex = new int[thisSchema.length];
        for(int g =0;g<ColorsIndex.length;g++){
            ColorsIndex[g]=AbstraktSelection.findColorIndex(thisSchema[g]);
        }
        int res = calculateAverage(ColorsIndex);
        imageMat = AbstraktSelection.replaceValue(imageMat,9999,res);
        return imageMat;
    }

    public static int calculateAverage(int[] array) {
        int sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum / array.length;
    }

    public static photo PhotoOptimither(photo image,int rez, int Q, double T, double S, double b){
        boolean work = true;
        int a =20;
        //b = 1.8;

        while (work) {
            image = AbstraktSelection.ReplaceColor(image);
            int[][] Sta1 = ThisRejim(rez);
            int[][] matImage = AbstraktSelection.convertToColorIndices(image);
            matImage = MatrixCheck(matImage, Sta1);
            if (contains9999(matImage)) {
                if(calculatePercentage(matImage)>Q) {//27
                    b+=T;//0.2-0.1
                    image = AbstraktSelection.EasySimplifier(image, b);
                }else if (calculatePercentage(matImage)>S) {//5
                    a -= 1;
                    BufferedImage img = image.toBufferedImage();
                    img = SimplifyColors.simplifyColors(img, a);
                    image = photo.fromBufferedImage(img);
                    image = AbstraktSelection.ReplaceColor(image);
                } else{
                    matImage = replace9999WithLeftNeighbor(matImage);
                    image = AbstraktSelection.fromIntMatrix(matImage);
                }

            }else {
                work = false;
            }
        }
        return image;
    }


    public static boolean contains9999(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                if (value == 9999) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int[][] replace9999WithLeftNeighbor(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 9999) {
                    matrix[i][j] = matrix[i][j-1];
                }
            }
        }
        return matrix;
    }

    public static double calculatePercentage(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Invalid matrix");
        }

        int totalElements = 0;
        int countOf99 = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                totalElements++;
                if (matrix[i][j] == 9999) {
                    countOf99++;
                }
            }
        }

        if (totalElements == 0) {
            throw new IllegalArgumentException("Empty matrix");
        }

        return ((double) countOf99 / totalElements) * 100.0;
    }

    public static int[][] MatrixCheck(int[][] StartImg,int[][] Sta) {
        int[][] FinishImg = StartImg;
        boolean[] StanokWork = new boolean[6];
        int[] WorkColor = new int[6];


        Sta[5][0] = backGround(StartImg);
        for (int i = 0; i < StartImg.length; i++) {
            for (int q = 0; q < StanokWork.length; q++) {
                if(Sta[q][0]!=-2) {
                    StanokWork[q] = false;
                }else {
                    StanokWork[q] = true;
                }
            }

            for (int j = 0; j < WorkColor.length; j++) {
                WorkColor[j] = -1;
            }

            for (int j = 0; j < StartImg[0].length; j++) {
                int s = StartImg[i][j];
                int[][] kordi = searchMatrix(Sta, s);
                if (kordi != null) {
                    int k = kordi.length;
                    if(k==1){
                        int g =0;
                        if(!isNowUsing(s,WorkColor,kordi, g)){
                            if(WorkColor[kordi[0][0]] == -1){
                                WorkColor[kordi[0][0]] = Sta[kordi[0][0]][kordi[0][1]];
                                StanokWork[kordi[0][0]] = true;
                            }else {
                                int[] notWork = findFalseIndices(StanokWork);
                                if (notWork != null) {
                                    boolean find = false;
                                    for (int q = 0; q < notWork.length; q++) {
                                        int u = notWork[q];
                                        int[] serchFool = Sta[u];
                                        if (isFool(serchFool)) {
                                            serchFool = replaceElement(serchFool,s);
                                            Sta[u] = serchFool;
                                            WorkColor[u] = s;
                                            StanokWork[u] = true;
                                            find = true;
                                            break;
                                        }
                                    }
                                    if(!find){
                                        StartImg[i][j] = 9999;
                                    }
                                } else {
                                    StartImg[i][j] = 9999;
                                }
                            }
                        }else {
                            if(!StanokWork[kordi[0][0]]){
                                StanokWork[kordi[0][0]] = true;
                            }
                        }
                    }else {
                        boolean stop = false;
                        for(int y=0;y<kordi.length;y++){
                            if(!isNowUsing(s,WorkColor,kordi, y)){
                                if(WorkColor[kordi[y][0]] == -1){
                                    WorkColor[kordi[y][0]] = Sta[kordi[y][0]][kordi[y][1]];
                                    StanokWork[kordi[y][0]] = true;
                                    stop = true;
                                }else {
                                    stop = false;
                                }
                            }else {
                                if(!StanokWork[kordi[y][0]]){
                                    StanokWork[kordi[y][0]] = true;
                                    stop = true;
                                }else {
                                    stop = true;
                                }
                            }

                            if(stop){
                                break;
                            }
                        }
                        if(!stop){
                            int[] notWork = findFalseIndices(StanokWork);
                            if (notWork != null) {
                                boolean find = false;
                                for (int q = 0; q < notWork.length; q++) {
                                    int u = notWork[q];
                                    int[] serchFool = Sta[u];
                                    if (isFool(serchFool)) {
                                        serchFool = replaceElement(serchFool,s);
                                        Sta[u] = serchFool;
                                        WorkColor[u] = s;
                                        StanokWork[u] = true;
                                        find = true;
                                        break;
                                    }
                                }
                                if(!find){
                                    StartImg[i][j] = 9999;
                                }
                            } else {
                                StartImg[i][j] = 9999;
                            }
                        }

                    }
                } else {
                    int[] notWork = findFalseIndices(StanokWork);
                    if (notWork != null) {
                        boolean find = false;
                        for (int q = 0; q < notWork.length; q++) {
                            int u = notWork[q];
                            int[] serchFool = Sta[u];
                            if (isFool(serchFool)) {
                                serchFool = replaceElement(serchFool,s);
                                Sta[u] = serchFool;
                                WorkColor[u] = s;
                                StanokWork[u] = true;
                                find = true;
                                break;
                            }
                        }
                        if(!find){
                            StartImg[i][j] = 9999;
                        }
                    } else {
                        StartImg[i][j] = 9999;
                    }
                }
            }
        }
        FinishImg = StartImg;
        return FinishImg;
    }


    public static void printBooleanArray(boolean[] array) {
        System.out.println("Значення масиву boolean: ");
        for (boolean value : array) {
            System.out.print(value + " ");
        }
    }

    public static void printIntArray(int[] array) {
        System.out.println();
        System.out.print("Значення масиву int: ");
        for (int value : array) {
            System.out.print(value + " ");
        }
    }

    public static int[] replaceElement(int[] array, int replacementValue){
        int targetValue = -1;
        for (int i=0;i<array.length;i++){
            if(array[i]==targetValue){
                array[i] = replacementValue;
                return array;
            }
        }
        return array;
}

    public static boolean isNowUsing(int s, int[] WorkColor, int[][] kordi, int g){
        if(s == WorkColor[kordi[g][0]]){
            return true;
        }else{
            return false;
        }
    }
    public static boolean isFool(int[] mas){
        for (int i=0;i< mas.length;i++){
            if (mas[i]==-1){
                return true;
            }
        }
        return false;
    }

    public static int backGround(int[][] image){
        int[][] procent = AbstraktSelection.createElementCountMatrix(image);
        sortMatrixBySecondColumn(procent);
        int b = procent[procent.length-1][0];
        return b;
    }

    public static int[] findFalseIndices(boolean[] array) {
        List<Integer> falseIndicesList = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            if (!array[i]) {
                falseIndicesList.add(i);
            }
        }

        // Перетворення List в int[]
        int[] falseIndices = new int[falseIndicesList.size()];
        for (int i = 0; i < falseIndicesList.size(); i++) {
            falseIndices[i] = falseIndicesList.get(i);
        }

        return falseIndices.length > 0 ? falseIndices : null;
    }

    public static void sortMatrixBySecondColumn(int[][] matrix) {
        // Використовуємо Comparator для порівняння за значеннями другого стовпця
        Arrays.sort(matrix, Comparator.comparingInt(arr -> arr[1]));
    }

    public static int[][] searchMatrix(int[][] matrix, int target) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        // Підрахунок кількості елементів, що відповідають умові
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == target) {
                    count++;
                }
            }
        }
        // Створення матриці для зберігання координат
        int[][] result = new int[count][2];

        // Заповнення матриці координат
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == target) {
                    result[index][0] = i;
                    result[index][1] = j;
                    index++;
                }
            }
        }
        return result.length > 0 ? result : null;
    }

    public static int findNumberPosition(int[] array, int number) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == number) {
                return i; // Знайдено число, повертаємо його позицію
            }
        }
        return -1; // Число не знайдено в масиві
    }

    public static photo trueStanock (photo Image, int rejim){
        Image = AbstraktSelection.ReplaceColor(Image);
        int[][] StartImg = AbstraktSelection.convertToColorIndices(Image);
        int[][] FinishImg = StartImg;
        boolean[] StanokWork = new boolean[6];
        int[] WorkColor = new int[6];

        int[][] Sta = ThisRejim(rejim);

        Sta[5][0] = backGround(StartImg);
        for (int i = 0; i < StartImg.length; i++) {
            for (int q = 0; q < StanokWork.length; q++) {
                if(Sta[q][0]!=-2) {
                    StanokWork[q] = false;
                }else {
                    StanokWork[q] = true;
                }
            }

            for (int j = 0; j < WorkColor.length; j++) {
                WorkColor[j] = -1;
            }

            for (int j = 0; j < StartImg[0].length; j++) {
                int s = StartImg[i][j];
                int[][] kordi = searchMatrix(Sta, s);
                if (kordi != null) {
                    int k = kordi.length;
                    if(k==1){
                        int g =0;
                        if(!isNowUsing(s,WorkColor,kordi, g)){
                            if(WorkColor[kordi[0][0]] == -1){
                                WorkColor[kordi[0][0]] = Sta[kordi[0][0]][kordi[0][1]];
                                StartImg[i][j] = 120000 + ((kordi[0][0] * 10) + kordi[0][1]);
                                StanokWork[kordi[0][0]] = true;
                            }else {
                                int[] notWork = findFalseIndices(StanokWork);
                                if (notWork != null) {
                                    boolean find = false;
                                    for (int q = 0; q < notWork.length; q++) {
                                        int u = notWork[q];
                                        int[] serchFool = Sta[u];
                                        if (isFool(serchFool)) {
                                            serchFool = replaceElement(serchFool,s);
                                            int targetValue = s;
                                            int r = findNumberPosition(serchFool, targetValue);
                                            Sta[u] = serchFool;
                                            WorkColor[u] = s;
                                            StanokWork[u] = true;
                                            find = true;
                                            StartImg[i][j] = 120000 + ((u * 10) + r);
                                            break;
                                        }
                                    }
                                    if(!find){
                                        StartImg[i][j] = 9999;
                                    }
                                } else {
                                    StartImg[i][j] = 9999;
                                }
                            }
                        }else {
                            if(!StanokWork[kordi[0][0]]){
                                StartImg[i][j] = 120000 + ((kordi[0][0] * 10) + kordi[0][1]);
                                StanokWork[kordi[0][0]] = true;
                            }else {
                                StartImg[i][j] = 120000 + ((kordi[0][0] * 10) + kordi[0][1]);
                            }
                        }
                    }else {
                        boolean stop = false;
                        for(int y=0;y<kordi.length;y++){
                            if(!isNowUsing(s,WorkColor,kordi, y)){
                                if(WorkColor[kordi[y][0]] == -1){
                                    WorkColor[kordi[y][0]] = Sta[kordi[y][0]][kordi[y][1]];
                                    StanokWork[kordi[y][0]] = true;
                                    StartImg[i][j] = 120000 + ((kordi[y][0] * 10) + kordi[y][1]);
                                    stop = true;
                                }else {
                                    stop = false;
                                }
                            }else {
                                if(!StanokWork[kordi[y][0]]){
                                    StanokWork[kordi[y][0]] = true;
                                    StartImg[i][j] = 120000 + ((kordi[y][0] * 10) + kordi[y][1]);
                                    stop = true;
                                }else {
                                    StartImg[i][j] = 120000 + ((kordi[y][0] * 10) + kordi[y][1]);
                                    stop = true;
                                }
                            }

                            if(stop){
                                break;
                            }
                        }
                        if(!stop){
                            int[] notWork = findFalseIndices(StanokWork);
                            if (notWork != null) {
                                boolean find = false;
                                for (int q = 0; q < notWork.length; q++) {
                                    int u = notWork[q];
                                    int[] serchFool = Sta[u];
                                    if (isFool(serchFool)) {
                                        serchFool = replaceElement(serchFool,s);
                                        int targetValue = s;
                                        int r = findNumberPosition(serchFool, targetValue);
                                        Sta[u] = serchFool;
                                        WorkColor[u] = s;
                                        StanokWork[u] = true;
                                        find = true;
                                        StartImg[i][j] = 120000 + ((u * 10) + r);
                                        break;
                                    }
                                }
                                if(!find){
                                    StartImg[i][j] = 9999;
                                }
                            } else {
                                StartImg[i][j] = 9999;
                            }
                        }

                    }
                } else {
                    int[] notWork = findFalseIndices(StanokWork);
                    if (notWork != null) {
                        boolean find = false;
                        for (int q = 0; q < notWork.length; q++) {
                            int u = notWork[q];
                            int[] serchFool = Sta[u];
                            if (isFool(serchFool)) {
                                serchFool = replaceElement(serchFool,s);
                                int targetValue = s;
                                int r = findNumberPosition(serchFool, targetValue);
                                Sta[u] = serchFool;
                                WorkColor[u] = s;
                                StanokWork[u] = true;
                                find = true;
                                StartImg[i][j] = 120000 + ((u * 10) + r);
                                break;
                            }
                        }
                        if(!find){
                            StartImg[i][j] = 9999;
                        }
                    } else {
                        StartImg[i][j] = 9999;
                    }
                }
            }
        }
        FinishImg = StartImg;
        Image.setStanokScheme(Sta);

        int[][] coutint = Converter.getIsExisten(FinishImg,119999);
        Color[][] Schema = AbstraktSelection.StanokSheme();

        for(int i=0;i<coutint.length;i++) {
            int[] cord = coutint[i];
            int cor = FinishImg[cord[0]][cord[1]];
            int x = (cor - 120000) / 10;
            int y = (cor - 120000) % 10;
            Color newColor = Schema[x][y];
            Image.setPixel(cord[0],cord[1],newColor);
        }
        return Image;
    }
}
