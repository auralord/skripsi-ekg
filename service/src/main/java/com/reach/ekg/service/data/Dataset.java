package com.reach.ekg.service.data;

import com.reach.ekg.service.util.RandomUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Dataset {

    private DataSource orig;
    private DataSource origNormalised;
    private DataSource trainingNomalised;
    private DataSource training;
    private DataSource testNormalised;
    private DataSource test;

    HashMap<Integer, List<Integer>> asTest;

    private Random r = RandomUtil.r();

    public Dataset(DataSource dataSource) {
        this.orig = dataSource;
        this.origNormalised = DataSources.normalise(dataSource);
    }

    public void randomize() {
        // Select which data selected as test
        int n = orig.count();
        int numClass = 4;
        int numTestPerClass = 4;
        int numPerClass = n / numClass;

        asTest = new HashMap<>();
        for (int i = 0; i < numClass; i++) {
            List<Integer> list = r
                    .ints(0, numPerClass)
                    .distinct()
                    .limit(numTestPerClass)
                    .boxed()
                    .collect(Collectors.toList());

            asTest.put(i, list);
        }

        // Fill (pig)
        int numTest = numClass * numTestPerClass;
        int numTraining = n - numTest;

        double[][] trainingData = new double[numTraining][];
        double[][] trainingDataNormalised = new double[numTraining][];
        int[] trainingTarget = new int[numTraining];

        double[][] testData = new double[numTest][];
        double[][] testDataNormalised = new double[numTest][];
        int[] testTarget = new int[numTest];

        int curTest = 0;
        int curTraining = 0;
        for (int i = 0; i < numClass; i++) {
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
