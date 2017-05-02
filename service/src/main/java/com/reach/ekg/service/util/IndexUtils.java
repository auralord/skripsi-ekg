package com.reach.ekg.service.util;

import java.util.List;
import java.util.function.IntToDoubleFunction;

public class IndexUtils {

    public static int max(double[] d) {
        int max = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] > d[max]) {
                max = i;
            }
        }
        return max;
    }

    public static int[] max(double[][] d) {
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                if (d[i][j] > d[maxX][maxY]) {
                    maxX = i;
                    maxY = j;
                }
            }
        }
        return new int[]{maxX, maxY};
    }

    public static int[] max(double[][] d, int[] indices) {
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < indices.length; i++) {
            for (int j = 0; j < indices.length; j++) {
                if (d[indices[i]][indices[j]] > d[maxX][maxY]) {
                    maxX = indices[i];
                    maxY = indices[j];
                }
            }
        }
        return new int[]{maxX, maxY};
    }

    public static int max(int[] indices, IntToDoubleFunction func) {
        int maxIndex = 0;
        double maxValue = func.applyAsDouble(maxIndex);
        for (int i = 0; i < indices.length; i++) {
            if (func.applyAsDouble(i) > maxValue) {
                maxIndex = i;
                maxValue = func.applyAsDouble(i);
            }
        }
        return maxIndex;
    }

    public static int[] listToArray(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int numOfTrue(boolean[] bools) {
        int count = 0;
        for (boolean b : bools ) {
            if (b) count++;
        }
        return count;
    }
}
