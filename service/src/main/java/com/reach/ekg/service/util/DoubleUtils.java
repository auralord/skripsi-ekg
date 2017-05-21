package com.reach.ekg.service.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class DoubleUtils {

    public static double sum(double... d) {
        return DoubleStream.of(d).sum();
    }

    public static double max(double... d) {
        return DoubleStream.of(d).max().getAsDouble();
    }

    public static double min(double... d) {
        return DoubleStream.of(d).min().getAsDouble();
    }

    public static double[] columnAvg(double[][] d) {
        return IntStream.range(0, d[0].length)
                .mapToDouble(i -> {
                    double avg = 0;
                    for (int j = 0; j < d.length; j++) {
                        avg += d[j][i];
                    }
                    return avg / d.length;
                }).toArray();
    }

    public static double squaredDistance(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IndexOutOfBoundsException();
        }

        return IntStream.range(0, x.length)
                .mapToDouble(i -> Math.pow(x[i] - y[i], 2))
                .sum();
    }

    public static double distance(double[] x, double[] y) {
        return Math.sqrt(squaredDistance(x, y));
    }

    public static double sumProduct(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IndexOutOfBoundsException();
        }

        return IntStream.range(0, x.length)
                .mapToDouble(i -> x[i] * y[i])
                .sum();
    }

    public static void printMatrix(double[][] d) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                System.out.printf("%10.6f ", d[i][j]);
            }
            System.out.println();
        }
    }

    public static void printArray(double[] d) {
        for (int i = 0; i < d.length; i++) {
            System.out.printf("%10.6f ", d[i]);
        }
        System.out.println();
    }

    public static void dumpMatrix(double[][] matrix) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("EEE, d MMM yyyy hh-mm-ss a");
        String fileName = "dump/" + LocalDateTime.now().format(f)+ ".csv";

        List<String> lines = new ArrayList<>();

        for (double[] d : matrix) {
            StringBuilder builder = new StringBuilder();
            for (double d1: d) {
                builder.append(d1).append(";");
            }
            lines.add(builder.toString());
        }

        try {
            Files.write(Paths.get(fileName), lines);
            System.out.println("File written: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

