package com.reach.ekg.service.classification.data;

import com.reach.ekg.service.util.DoubleUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.*;

public class DataSources {
    public static DataSource fromCSV(String csvPath, String delimiter, int indexCol,
                                     int classRow, int dataColStart, int dataLength) {
        Path file = Paths.get(csvPath);
        try {
            List<String> lines = Files.readAllLines(file);
            int rows = lines.size();

            int[] index = new int[rows];
            double[][] data = new double[rows][];
            int[] target = new int[rows];

            for (int i = 0; i < rows; i++) {
                double[] currentLine = Stream
                        .of(lines.get(i).split(delimiter))
                        .mapToDouble(Double::parseDouble)
                        .toArray();

                index[i] = (int) currentLine[indexCol];
                data[i] = new double[dataLength];
                System.arraycopy(currentLine, dataColStart, data[i], 0, dataLength);
                target[i] = (int) currentLine[classRow];
            }

            return new ArrayDataSource(index, data, target);
        } catch (IOException e) {
            return null;
        }
    }

    public static DataSource subFeatures(DataSource ds, boolean[] features) {
        int count = 0;
        for (boolean b : features) {
            if (b) count++;
        }

        double[][] data = new double[ds.count()][count];
        for (int i = 0; i < ds.count(); i++) {
            double[] d = ds.row(i);
            data[i] = IntStream.range(0, features.length)
                    .filter(x -> features[x])
                    .mapToDouble(x -> d[x])
                    .toArray();
        }

        return new ArrayDataSource(ds.indices(), data, ds.targets());
    }

    public static DataSource toBinary(DataSource ds, int[] plusClasses, int[] minusClasses) {
        List<Integer> pluses = IntStream.of(plusClasses).distinct().boxed()
                .collect(Collectors.toList());

        List<Integer> minuses = IntStream.of(minusClasses).distinct().boxed()
                .collect(Collectors.toList());

        int[] selectedRows = IntStream.range(0, ds.count())
                .filter(i -> pluses.contains(ds.target(i)) || minuses.contains(ds.target(i)))
                .toArray();

        double[][] data = new double[selectedRows.length][];
        for (int i = 0; i < selectedRows.length; i++) {
            data[i] = ds.row(selectedRows[i]);
        }
        int[] target = IntStream.of(selectedRows).map(i -> {
            if (pluses.contains(ds.target(i))) {
                return 1;
            } if (minuses.contains(ds.target(i))) {
                return -1;
            } else {
                return 0;
            }
        }).toArray();

        return new ArrayDataSource(data, target);
    }

    public static DataSource normalise(DataSource dataSource) {
        int rows = dataSource.count();
        int cols = dataSource.row(0).length;

        double[] max = new double[cols];
        double[] min = new double[cols];
        for (int i = 0; i < cols; i++) {
            max[i] = DoubleUtils.max(dataSource.column(i));
            min[i] = DoubleUtils.min(dataSource.column(i));
        }

        int[] newTarget = dataSource.targets();
        double[][] newData = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newData[i][j] = (dataSource.get(i, j) - min[j]) / (max[j] - min[j]);
            }
        }

        return new ArrayDataSource(newData, newTarget);
    }

//    public static void main(String[] args) {
//        DataSource d1 = fromCSV("data/manualisasi.csv", ";", 0, 1, 2, 16);
//        DataSource d2 = normalise(d1);
//        System.out.println("Kek 1: ");
//        DoubleUtils.printMatrix(d1.all());
//        System.out.println("Kek 2: ");
//        DoubleUtils.printMatrix(d2.all());
//    }
}
