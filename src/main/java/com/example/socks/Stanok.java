package com.example.socks;

import java.awt.*;
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

    public static photo main(photo image,boolean optimathe,int rejim) {
        int[][] Sta = ThisRejim(rejim);

        if(optimathe){
            image = PhotoOptimither(image,Sta);
        }else {
            int[][] imageMat = AbstraktSelection.convertToColorIndices(image);
            int m = image.getM();
            int n = image.getN();
            imageMat = MatrixCheck(imageMat, n, m,Sta);
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
        imageMat = AbstraktSelection.replaceValue(imageMat,99,res);
        return imageMat;
    }

    public static int calculateAverage(int[] array) {
        int sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum / array.length;
    }

    public static photo PhotoOptimither(photo image,int[][] Sta){
        boolean work = true;
        double a =1;
        while (work) {
            a+=0.3;
            int[][] matImage = AbstraktSelection.convertToColorIndices(image);
            matImage = MatrixCheck(matImage, matImage[0].length, matImage.length, Sta);
            if (contains99(matImage)) {
                image = AbstraktSelection.EasySimplifier(image,a);
            }else {
                work = false;
            }
        }
        int[][] matImage = AbstraktSelection.convertToColorIndices(image);
        image.setStanokScheme(StanokSchemeCheck(matImage, matImage[0].length, matImage.length, Sta));
        return image;
    }


    public static boolean contains99(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                if (value == 99) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int[][] MatrixCheck(int[][] StartImg, int m, int n,int[][] Sta) {
        int[][] FinishImg = StartImg;
        boolean[] StanokWork = new boolean[6];
        int[] WorkColor = new int[6];
        for (int j = 0; j < WorkColor.length; j++) {
            WorkColor[j] = -1;
        }
        Sta[5][0] = backGround(StartImg);
        for (int i = 0; i < n; i++) {

            for (int q = 0; q < StanokWork.length; q++) {
                StanokWork[q] = false;
            }

            for (int j = 0; j < WorkColor.length; j++) {
                WorkColor[j] = -1;
            }

            for (int j = 0; j < m; j++) {
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
                                    for (int q = 0; q < notWork.length; q++) {
                                        int u = notWork[g];
                                        int[] serchFool = Sta[u];
                                        if (isFool(serchFool)) {
                                            serchFool = replaceElement(serchFool,s);
                                            Sta[u] = serchFool;
                                            WorkColor[u] = s;
                                            StanokWork[u] = true;
                                            break;
                                        }else {
                                            StartImg[i][j] = 99;
                                        }
                                    }
                                } else {
                                    StartImg[i][j] = 99;
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
                                if(WorkColor[kordi[0][0]] == -1){
                                    WorkColor[kordi[0][0]] = Sta[kordi[0][0]][kordi[0][1]];
                                    StanokWork[kordi[0][0]] = true;
                                    stop = true;
                                }
                            }else {
                                if(!StanokWork[kordi[0][0]]){
                                    StanokWork[kordi[0][0]] = true;
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
                                for (int g = 0; g < notWork.length; g++) {
                                    int h = notWork[g];
                                    int[] serchFool = Sta[h];
                                    if (isFool(serchFool)) {
                                        serchFool = replaceElement(serchFool,s);
                                        Sta[h] = serchFool;
                                        WorkColor[h] = s;
                                        StanokWork[h] = true;
                                        break;
                                    }else {
                                        StartImg[i][j] = 99;
                                    }
                                }
                            } else {
                                StartImg[i][j] = 99;
                            }
                        }

                    }
                } else {
                    int[] notWork = findFalseIndices(StanokWork);
                    if (notWork != null) {
                        for (int g = 0; g < notWork.length; g++) {
                            int k = notWork[g];
                            int[] serchFool = Sta[k];
                            if (isFool(serchFool)) {
                                serchFool = replaceElement(serchFool,s);
                                Sta[k] = serchFool;
                                WorkColor[k] = s;
                                StanokWork[k] = true;
                                break;
                            }else {
                                StartImg[i][j] = 99;
                            }
                        }
                    } else {
                        StartImg[i][j] = 99;
                    }
                }
            }
        }
        FinishImg = StartImg;
        return FinishImg;
    }



    public static int[][] StanokSchemeCheck(int[][] StartImg, int m, int n,int[][] Sta) {
        int[][] FinishImg = StartImg;
        boolean[] StanokWork = new boolean[6];
        int[] WorkColor = new int[6];
        for (int j = 0; j < WorkColor.length; j++) {
            WorkColor[j] = -1;
        }
        Sta[5][0] = backGround(StartImg);
        for (int i = 0; i < n; i++) {

            for (int q = 0; q < StanokWork.length; q++) {
                StanokWork[q] = false;
            }

            for (int j = 0; j < WorkColor.length; j++) {
                WorkColor[j] = -1;
            }

            for (int j = 0; j < m; j++) {
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
                                    for (int q = 0; q < notWork.length; q++) {
                                        int u = notWork[g];
                                        int[] serchFool = Sta[u];
                                        if (isFool(serchFool)) {
                                            serchFool = replaceElement(serchFool,s);
                                            Sta[u] = serchFool;
                                            WorkColor[u] = s;
                                            StanokWork[u] = true;
                                            break;
                                        }else {
                                            StartImg[i][j] = 99;
                                        }
                                    }
                                } else {
                                    StartImg[i][j] = 99;
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
                                if(WorkColor[kordi[0][0]] == -1){
                                    WorkColor[kordi[0][0]] = Sta[kordi[0][0]][kordi[0][1]];
                                    StanokWork[kordi[0][0]] = true;
                                    stop = true;
                                }
                            }else {
                                if(!StanokWork[kordi[0][0]]){
                                    StanokWork[kordi[0][0]] = true;
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
                                for (int g = 0; g < notWork.length; g++) {
                                    int h = notWork[g];
                                    int[] serchFool = Sta[h];
                                    if (isFool(serchFool)) {
                                        serchFool = replaceElement(serchFool,s);
                                        Sta[h] = serchFool;
                                        WorkColor[h] = s;
                                        StanokWork[h] = true;
                                        break;
                                    }else {
                                        StartImg[i][j] = 99;
                                    }
                                }
                            } else {
                                StartImg[i][j] = 99;
                            }
                        }

                    }
                } else {
                    int[] notWork = findFalseIndices(StanokWork);
                    if (notWork != null) {
                        for (int g = 0; g < notWork.length; g++) {
                            int k = notWork[g];
                            int[] serchFool = Sta[k];
                            if (isFool(serchFool)) {
                                serchFool = replaceElement(serchFool,s);
                                Sta[k] = serchFool;
                                WorkColor[k] = s;
                                StanokWork[k] = true;
                                break;
                            }else {
                                StartImg[i][j] = 99;
                            }
                        }
                    } else {
                        StartImg[i][j] = 99;
                    }
                }
            }
        }
        return Sta;
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

}
