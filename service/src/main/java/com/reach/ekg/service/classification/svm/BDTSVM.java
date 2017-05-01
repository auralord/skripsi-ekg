package com.reach.ekg.service.classification.svm;

import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.classification.data.DataSources;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


import static com.reach.ekg.service.util.DoubleUtils.*;
import static com.reach.ekg.service.util.IndexUtils.listToArray;
import static com.reach.ekg.service.util.IndexUtils.max;
import static java.lang.Math.pow;
import static java.lang.Math.min;
import static java.lang.Math.max;

public class BDTSVM {

    static class BDTNode {
        BDTNode right;
        BDTNode left;
        List<Integer> data;
        BinarySVM svm;

        BDTNode() {
            this.data = new ArrayList<>();
        }

        @Override
        public String toString() {
            if (data.size() == 1) {
                return data.toString();
            } else {
                return data.toString() + "{" +
                        left.toString() + " - " +
                        right.toString() + "}";
            }
        }
    }

    private BDTNode root;

    private SVMParams params;
    private DataSource training;
    private DataSource normalised;

    private Kernel k;
    private double[][] distance;
    private double[][] classAvg;
    private double[][] kernel;

    public BDTSVM(SVMParams params) {
        this.params = params;
    }

    public void train() {
        // Calculate kernel
//        k = (x, y) -> pow(sumProduct(x, y), 2);
        k = (x, y) -> Math.exp(-squaredDistance(x, y) / (2 * pow(10, 2)));
        int n = normalised.count();

        kernel = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                kernel[j][i] = kernel[i][j] = k.apply(normalised.row(i), normalised.row(j));
            }
            kernel[i][i] = k.apply(normalised.row(i), normalised.row(i));
        }

        // Do the rest
        generateBDT();

//        root = new BDTNode();
//        root.data.add(0);
//        root.data.add(1);
//        root.data.add(2);
//        root.data.add(3);
//        root.left = new BDTNode();
//        root.left.data.add(0);
//
//        root.right = new BDTNode();
//        root.right.data.add(1);
//        root.right.data.add(2);
//        root.right.data.add(3);
//
//        BDTNode pointer = root.right;
//        pointer.left = new BDTNode();
//        pointer.left.data.add(1);
//        pointer.left.data.add(2);
//
//        pointer.right = new BDTNode();
//        pointer.right.data.add(3);
//
//        pointer = pointer.left;
//        pointer.left = new BDTNode();
//        pointer.left.data.add(1);
//        pointer.right = new BDTNode();
//        pointer.right.data.add(2);

        train(root);
    }

    private void generateBDT() {
//        java.util.Date d1 = new java.util.Date();
        root = new BDTNode();

        int numClass = training.numClass();
        distance = new double[numClass][numClass];
        classAvg = new double[numClass][];

        for (int i = 0; i < numClass; i++) {
            classAvg[i] = columnAvg(training.getAllRecordsInClass(i));
        }
//
        for (int i = 0; i < numClass; i++) {
            for (int j = 0; j < numClass; j++) {
                distance[i][j] = distance(classAvg[i], classAvg[j]);
            }
            root.data.add(i);
        }

        processNode(root);
//        System.out.println(root);
//        java.util.Date d2 = new java.util.Date();
//        System.out.println("generating bdt: " + (d2.getTime() - d1.getTime()));
    }

    private void processNode(BDTNode node) {
        int[] indices = listToArray(node.data);
        int[] furthest = max(distance, indices);
        int left = min(furthest[0], furthest[1]);
        int right = max(furthest[0], furthest[1]);

        node.left = new BDTNode();
        node.left.data.add(left);
        node.right = new BDTNode();
        node.right.data.add(right);

        List<Integer> temp = new ArrayList<>(node.data);
        temp.remove(new Integer(left));
        temp.remove(new Integer(right));

        double[] gravityLeft = classAvg[left];
        double[] gravityRight = classAvg[right];

        for (int i : temp) {
            double distanceToLeft = distance(gravityLeft, classAvg[i]);
            double distanceToRight = distance(gravityRight, classAvg[i]);
            if (distanceToLeft < distanceToRight) {
                node.left.data.add(i);
                gravityLeft = gravityCenter(listToArray(node.left.data));
            } else {
                node.right.data.add(i);
                gravityRight = gravityCenter(listToArray(node.right.data));
            }
        }

        if (node.left.data.size() > 1) {
            processNode(node.left);
        }

        if (node.right.data.size() > 1) {
            processNode(node.right);
        }
    }

    private double[] gravityCenter(int... ints) {
        double[][] gc = new double[ints.length][];
        for (int i = 0; i < ints.length; i++) {
            gc[i] = classAvg[ints[i]];
        }
        return columnAvg(gc);

    }

    private void train(BDTNode node) {
        if (node.data.size() == 1) return;

        node.svm = new BinarySVM(params, k);

        DataSource dataSource = DataSources.toBinary(normalised,
                listToArray(node.left.data),
                listToArray(node.right.data)
        );
        node.svm.kernel(setupKernel(
                node.left.data,
                node.right.data
        ));
        node.svm.train(dataSource);

        if (node.left != null) {
            train(node.left);
        }

        if (node.right != null) {
            train(node.right);
        }
    }

    private double[][] setupKernel(List<Integer> left, List<Integer> right) {
        int n = normalised.count();

        int[] indices = IntStream.range(0, n).filter(i -> {
            int c = normalised.target(i);
            return left.contains(c) || right.contains(c);
        }).toArray();
        int m = indices.length;

        double[][] newKernel = new double[m][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                newKernel[i][j] = kernel[indices[i]][indices[j]];
            }
        }
        return newKernel;
    }

    public int test(double... x) {
        int result = 0;
        BDTNode current = root;
        while (current.data.size() > 1) {
            int y = current.svm.test(x);
            if (y == -1) {
                current = current.right;
            } else if (y == 1) {
                current = current.left;
            } else {
                throw new IndexOutOfBoundsException();
            }

            if (current.data.size() == 1) {
                result = current.data.get(0);
            }
        }
        return result;
    }

    public void setTraining(DataSource training) {
        this.training = training;
    }

    public void setTrainingNormalised(DataSource normalised) {
        this.normalised = normalised;
    }

//    public static void main(String[] args) {
//
//        DataSource ds = DataSources.fromCSV("data/training.csv", ";", 0, 1, 2, 16);
//        boolean[] sf = {true, false, true, true, false, true, true, true, false, false, false, true, false, true, true, false};
//
//        DataSource training = DataSources.subFeatures(ds, sf);
//        DataSource normalised = DataSources.fromCSV("data/normalised.csv", ";", 0, 1, 2, 9);
//
//        java.util.Date d1 = new java.util.Date();
//        BDTSVM svm = new BDTSVM(new SVMParams()
//                .setLambda(0.5)
//                .setGamma(0.01)
//                .setC(1)
//                .setEpsilon(0.000001)
//                .setThreshold(0)
//                .setMaxIter(2)
//                .setKernelParam(2));
//        svm.setTrainingNormalised(normalised);
//        svm.setTraining(training);
//        svm.train();
//
//        double[] data1 = {1, 1, 0.470588235, 0.537572254, 0.327868852, 0.656923077, 0.930434783, 0.609375, 0.518518519};
//        double[] data2 = {0.18313253, 0.839116719, 0.651260504, 0.549132948, 0.495081967, 0.603076923, 1, 0.612132353, 0.57037037};
//        double[] data3 = {0.363855422, 0.678233438, 0.159663866, 0.219653179, 0.209836066, 0.518461538, 0, 0, 0.466666667};
//        double[] data4 = {0.291566265, 0.867507886, 0.575630252, 0.468208092, 0.357377049, 0.572307692, 0.208695652, 0.546875, 0.340740741};
//
//        int y1 = svm.test(data1);
//        int y2 = svm.test(data2);
//        int y3 = svm.test(data3);
//        int y4 = svm.test(data4);
//
//        System.out.println(y1);
//        System.out.println(y2);
//        System.out.println(y3);
//        System.out.println(y4);
//
//        java.util.Date d2 = new java.util.Date();
//        System.out.println(d2.getTime() - d1.getTime());
//    }
}