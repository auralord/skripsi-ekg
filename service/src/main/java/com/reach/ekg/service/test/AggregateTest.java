package com.reach.ekg.service.test;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.IndividualTestResult;

import java.util.ArrayList;
import java.util.List;
import static com.reach.ekg.service.util.DateUtils.now;

public class AggregateTest {

    private SVMParams svmParams;
    private GAParams gaParams;
    private AggregateTestResult result;

    public AggregateTest(SVMParams svmParams, GAParams gaParams) {
        this.svmParams = svmParams;
        this.gaParams = gaParams;
    }

    public void run(int repeat) {
        List<IndividualTestResult> individualResults = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            IndividualTest test = new IndividualTest(svmParams, gaParams);
            test.run();
            individualResults.add(test.getResult());
        }

        result = new AggregateTestResult(now(), svmParams, gaParams, individualResults);
    }

    public AggregateTestResult getResult() {
        return result;
    }
}
