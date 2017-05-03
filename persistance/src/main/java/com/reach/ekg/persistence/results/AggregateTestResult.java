package com.reach.ekg.persistence.results;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;

import java.util.List;
import java.util.OptionalDouble;

public class AggregateTestResult {

    private String time;

    private GAParams gaParams;
    private SVMParams svmParams;
    private List<IndividualTestResult> results;

    private double averageAccuracy;

    public AggregateTestResult() {
    }

    public AggregateTestResult(String time,
                               SVMParams svmParams,
                               GAParams gaParams,
                               List<IndividualTestResult> results) {

        this.time = time;
        this.gaParams = gaParams;
        this.svmParams = svmParams;
        this.results = results;
        calculateAccuracy();
    }

    private void calculateAccuracy() {
        OptionalDouble acc = results.stream()
                .mapToDouble(IndividualTestResult::getAccuracy)
                .average();

        averageAccuracy = acc.isPresent() ? acc.getAsDouble() : 0;
    }

    public String getTime() {
        return time;
    }

    public GAParams getGaParams() {
        return gaParams;
    }

    public void setGaParams(GAParams gaParams) {
        this.gaParams = gaParams;
    }

    public SVMParams getSvmParams() {
        return svmParams;
    }

    public void setSvmParams(SVMParams svmParams) {
        this.svmParams = svmParams;
    }

    public List<IndividualTestResult> getResults() {
        return results;
    }

    public void setResults(List<IndividualTestResult> results) {
        this.results = results;
    }

    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    @Override
    public String toString() {
        return "AggregateTestResult{" +
                ", time='" + time + '\'' +
                ", gaParams=" + gaParams +
                ", svmParams=" + svmParams +
                ", results=" + results +
                ", averageAccuracy=" + averageAccuracy +
                '}';
    }
}
