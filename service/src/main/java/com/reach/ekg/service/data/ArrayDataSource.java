package com.reach.ekg.service.data;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArrayDataSource implements DataSource {

    // Dataset
    private int[] index;
    private double[][] data;
    private int[] target;
    private int numClass;
    private int numData;

    public ArrayDataSource() {
        this.index = new int[]{1};
        this.data = new double[1][1];
        this.target = new int[1];
        this.numData = 1;
        this.numClass = 1;
    }

    public ArrayDataSource(double[][] data, int[] target) {
        this.index = IntStream.range(0, data.length).toArray();
        this.data = data;
        this.target = target;
        numData = data.length;
        numClass = (int) IntStream.of(target).distinct().count();
    }

    public ArrayDataSource(int[] index, double[][] data, int[] target) {
        this.index = index;
        this.data = data;
        this.target = target;
        numData = data.length;
        numClass = (int) IntStream.of(target).distinct().count();
    }

    @Override
    public double[][] all() {
        return data;
    }

    @Override
    public double get(int row, int col) {
        return data[row][col];
    }

    @Override
    public int count() {
        return data.length;
    }

    @Override
    public double[] row(int i) {
        return data[i];
    }

    @Override
    public double[] column(int i) {
        return Stream.of(data).mapToDouble(r -> r[i]).toArray();
    }

    @Override
    public int[] indices() {
        return index;
    }

    @Override
    public int index(int row) {
        return index[row];
    }

    @Override
    public int[] targets() {
        return target;
    }

    @Override
    public int target(int row) {
        return target[row];
    }

    @Override
    public double[][] getAllRecordsInClass(int _class) {
        int[] selected = IntStream.range(0, numData)
                .filter(i -> target[i] == _class)
                .toArray();

        double[][] result = new double[selected.length][];
        for (int i = 0; i < selected.length; i++) {
            result[i] = data[selected[i]];
        }

        return result;
    }

    @Override
    public int numClass() {
        return numClass;
    }

    public void setData(double[][] data) {
        this.data = data;
        numData = data.length;
    }

    public void setTarget(int[] target) {
        this.target = target;
        numClass = (int) IntStream.of(target).distinct().count();
    }

    public void setIndex(int[] index) {
        this.index = index;
    }
}
