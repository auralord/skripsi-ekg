package com.reach.ekg.service.test;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.service.classification.data.Dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.reach.ekg.service.util.DateUtils.now;

public class AggregateTest {

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

            switch(validation) {
                case "kfold": test.setKFold(getFold(i)); break;
                case "loo"  : test.setLeaveOneOut(i); break;
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

    public AggregateTestResult getResult() {
        return result;
    }
}
