package com.reach.ekg.service.classification.data;

import com.reach.ekg.service.util.RandomUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Dataset {

    public final static int NUM_OF_CLASSES = 4;
    public final static int TEST_PER_CLASSES = 5;

    private DataSource orig;
    private DataSource origNormalised;
    private DataSource trainingNomalised;
    private DataSource training;
    private DataSource testNormalised;
    private DataSource test;

    private HashMap<Integer, List<Integer>> asTest;

    private Random r = RandomUtil.r();

    public Dataset(DataSource dataSource) {
        this.orig = dataSource;
        this.origNormalised = DataSources.normalise(dataSource);
    }

    public void randomize() {
        // Select which data selected as test
        int n = orig.count();
        int numPerClass = n / NUM_OF_CLASSES;

        asTest = new HashMap<>();
        for (int i = 0; i < NUM_OF_CLASSES; i++) {
            List<Integer> list = r
                    .ints(0, numPerClass)
                    .distinct()
                    .limit(TEST_PER_CLASSES)
                    .boxed()
                    .collect(Collectors.toList());

            asTest.put(i, list);
        }

        // Fill (pig)
        separateTestTraining();
    }

    public void setTest(HashMap<Integer, List<Integer>> asTest) {
        System.out.println("WARNING: data has been set manually");
        this.asTest = asTest;
        separateTestTraining();
    }

    public void setLeaveOneOut(int i) {
        int total = orig.count() - 1;
        double[][] trainingData = new double[total][];
        double[][] trainingDataNormalised = new double[total][];
        int[] trainingTarget = new int[total];

        double[][] testData = new double[1][];
        double[][] testDataNormalised = new double[1][];
        int[] testTarget = new int[1];

        int current = 0;
        for (int j = 0; j < orig.count(); j++) {
            if (j == 1) {
                testData[0] = orig.row(i);
                testDataNormalised[0] = origNormalised.row(i);
                testTarget[0] = orig.target(i);
            } else {
                trainingData[current] = orig.row(i);
                trainingDataNormalised[current] = origNormalised.row(i);
                trainingTarget[current] = orig.target(i);
                current++;
            }
        }

        training = new ArrayDataSource(trainingData, trainingTarget);
        trainingNomalised = new ArrayDataSource(trainingDataNormalised, trainingTarget);
        test = new ArrayDataSource(testData, testTarget);
        testNormalised = new ArrayDataSource(testDataNormalised, testTarget);
    }

    private void separateTestTraining() {
        int n = orig.count();
        int numPerClass = n / NUM_OF_CLASSES;

        int numTest = NUM_OF_CLASSES * TEST_PER_CLASSES;
        int numTraining = n - numTest;

        double[][] trainingData = new double[numTraining][];
        double[][] trainingDataNormalised = new double[numTraining][];
        int[] trainingTarget = new int[numTraining];

        double[][] testData = new double[numTest][];
        double[][] testDataNormalised = new double[numTest][];
        int[] testTarget = new int[numTest];

        int curTest = 0;
        int curTraining = 0;
        for (int i = 0; i < NUM_OF_CLASSES; i++) {
            double[][] curData = orig.getAllRecordsInClass(i);
            double[][] curDataNormalised = origNormalised.getAllRecordsInClass(i);
            List<Integer> curList = asTest.get(i);

            for (int j = 0; j < numPerClass; j++) {
                if (curList.contains(j)) {
                    testData[curTest] = curData[j];
                    testDataNormalised[curTest] = curDataNormalised[j];
                    testTarget[curTest] = i;
                    curTest++;
                } else {
                    trainingData[curTraining] = curData[j];
                    trainingDataNormalised[curTraining] = curDataNormalised[j];
                    trainingTarget[curTraining] = i;
                    curTraining++;
                }
            }
        }

        training = new ArrayDataSource(trainingData, trainingTarget);
        trainingNomalised = new ArrayDataSource(trainingDataNormalised, trainingTarget);
        test = new ArrayDataSource(testData, testTarget);
        testNormalised = new ArrayDataSource(testDataNormalised, testTarget);
    }

    public DataSource getTraining() {
        return training;
    }

    public DataSource getTrainingNomalised() {
        return trainingNomalised;
    }

    public DataSource getTest() {
        return test;
    }

    public DataSource getTestNormalised() {
        return testNormalised;
    }

    public HashMap<Integer, List<Integer>> getAsTest() {
        return asTest;
    }
}
