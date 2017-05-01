package com.reach.ekg.service.classification.data;

public interface DataSource {

    double[][] all();

    double get(int row, int col);

    int count();

    double[] row(int i);

    double[] column(int i);

    int[] indices();

    int index(int row);

    int[] targets();

    int target(int row);

    double[][] getAllRecordsInClass(int _class);

    int numClass();

}
