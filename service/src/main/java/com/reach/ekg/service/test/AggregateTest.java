package com.reach.ekg.service.test;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.service.classification.data.Dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.reach.ekg.service.util.DateUtils.now;

public class AggregateTest {

    private static final String RANDOM = "random";
    private static final String LOO = "loo";
    private static final String KFOLD = "kfold";
    private static final String FIXED = "fixed";

    private String label;
    private String time;
    private SVMParams svmParams;
    private GAParams gaParams;
    private AggregateTestResult result;

    private String validation;

    private Runnable postIteration;

    public AggregateTest(String label, SVMParams svmParams, GAParams gaParams, String validation) {
        this(label, svmParams, gaParams);
        this.validation = validation;
    }

    public AggregateTest(String label, SVMParams svmParams, GAParams gaParams) {
        this.time = now();
        this.label = label;
        this.svmParams = svmParams;
        this.gaParams = gaParams;
    }

    public void postIteration(Runnable c) {
        this.postIteration = c;
    }

    public void run(int repeat) {
        List<IndividualTestResult> individualResults = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            IndividualTest test = new IndividualTest(svmParams, gaParams);

            switch (validation) {
                case KFOLD:
                    test.dataset().setTest(getFold(i));
                    break;
                case LOO:
                    test.dataset().setLeaveOneOut(i);
                    break;
                case RANDOM:
                    test.dataset().randomize();
                    break;
                case FIXED:
                    test.dataset().setTest(fixed(i));
                    break;
                default:
                    test.dataset().setTest(fixed(0));
            }

            test.run();
            individualResults.add(test.getResult());
            if (postIteration != null) postIteration.run();
        }

        result = new AggregateTestResult(
                label,
                time,
                svmParams,
                gaParams,
                individualResults
        );
    }

    private HashMap<Integer, List<Integer>> getFold(int i) {
        HashMap<Integer, List<Integer>> map = new HashMap<>();
        int numClasses = Dataset.NUM_OF_CLASSES;
        int numTests = Dataset.TEST_PER_CLASSES;

        for (int j = 0; j < numClasses; j++) {
            List<Integer> list = new ArrayList<>();
            for (int k = 0; k < numTests; k++) list.add(i * numTests + k);
            map.put(j, list);
        }

        return map;
    }

    private HashMap<Integer, List<Integer>> fixed(int i) {
        HashMap<Integer, List<Integer>> testData = new HashMap<>();
        switch (i) {
            case 0:
                testData.put(0, Arrays.asList(19, 34, 28, 13, 10));
                testData.put(1, Arrays.asList(26, 6, 25, 1, 22));
                testData.put(2, Arrays.asList(31, 30, 23, 25, 13));
                testData.put(3, Arrays.asList(12, 32, 17, 4, 13));
                break;
            case 1:
                testData.put(0, Arrays.asList(34, 1, 11, 3, 17));
                testData.put(1, Arrays.asList(14, 8, 26, 25, 28));
                testData.put(2, Arrays.asList(30, 18, 3, 19, 2));
                testData.put(3, Arrays.asList(0, 1, 31, 34, 12));
                break;
            case 2:
                testData.put(0, Arrays.asList(29, 2, 33, 22, 12));
                testData.put(1, Arrays.asList(26, 28, 33, 12, 0));
                testData.put(2, Arrays.asList(21, 19, 33, 1, 24));
                testData.put(3, Arrays.asList(21, 1, 19, 12, 18));
                break;
            case 3:
                testData.put(0, Arrays.asList(33, 25, 17, 19, 22));
                testData.put(1, Arrays.asList(30, 16, 24, 19, 23));
                testData.put(2, Arrays.asList(10, 24, 13, 31, 22));
                testData.put(3, Arrays.asList(14, 21, 23, 2, 17));
                break;
            case 4:
                testData.put(0, Arrays.asList(11, 26, 8, 34, 30));
                testData.put(1, Arrays.asList(3, 26, 29, 32, 9));
                testData.put(2, Arrays.asList(21, 16, 25, 12, 11));
                testData.put(3, Arrays.asList(27, 17, 5, 0, 7));
                break;
            case 5:
                testData.put(0, Arrays.asList(7, 22, 11, 15, 0));
                testData.put(1, Arrays.asList(21, 5, 30, 22, 26));
                testData.put(2, Arrays.asList(14, 34, 6, 19, 16));
                testData.put(3, Arrays.asList(21, 31, 9, 16, 1));
                break;
            case 6:
                testData.put(0, Arrays.asList(23, 5, 14, 2, 18));
                testData.put(1, Arrays.asList(23, 12, 6, 25, 21));
                testData.put(2, Arrays.asList(9, 12, 5, 18, 7));
                testData.put(3, Arrays.asList(9, 1, 8, 10, 25));
                break;
            case 7:
                testData.put(0, Arrays.asList(18, 17, 26, 16, 22));
                testData.put(1, Arrays.asList(27, 21, 13, 6, 32));
                testData.put(2, Arrays.asList(21, 22, 5, 15, 32));
                testData.put(3, Arrays.asList(32, 24, 8, 15, 23));
                break;
            case 8:
                testData.put(0, Arrays.asList(19, 25, 3, 14, 23));
                testData.put(1, Arrays.asList(3, 19, 23, 9, 20));
                testData.put(2, Arrays.asList(32, 17, 30, 26, 4));
                testData.put(3, Arrays.asList(14, 32, 21, 19, 8));
                break;
            case 9:
                testData.put(0, Arrays.asList(29, 17, 11, 23, 2));
                testData.put(1, Arrays.asList(24, 14, 19, 32, 31));
                testData.put(2, Arrays.asList(15, 9, 5, 22, 2));
                testData.put(3, Arrays.asList(30, 9, 6, 4, 5));
                break;


        }
        return testData;
    }

    public AggregateTestResult getResult() {
        return result;
    }
}
