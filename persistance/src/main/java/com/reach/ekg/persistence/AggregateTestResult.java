package com.reach.ekg.persistence;

import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;

import java.util.List;
import java.util.OptionalDouble;

public class AggregateTestResult {

    private GAParams gaParams;
    private SVMParams svmParams;
    private List<IndividualTestResult> results;

    private double averageAccuracy;

    public AggregateTestResult() {}

    public AggregateTestResult(GAParams gaParams, SVMParams svmParams,
                               List<IndividualTestResult> results) {
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
                "gaParams=" + gaParams +
                ", svmParams=" + svmParams +
                ", results=" + results +
                ", averageAccuracy=" + averageAccuracy +
                '}';
    }

//    public static void main(String[] args) {
//
//        List<Double> history = new ArrayList<>();
//        history.add(0.1);
//        history.add(0.34);
//        history.add(0.88);
//
//        boolean[] features = {
//                true, false, true,
//                true, false, true,
//                true, false, true,
//                true, false, true
//        };
//
//        List<ClassificationResult> classificationResults = new ArrayList<>();
//        classificationResults.add(new ClassificationResult(1, 2));
//        classificationResults.add(new ClassificationResult(2, 2));
//        classificationResults.add(new ClassificationResult(3, 3));
//        classificationResults.add(new ClassificationResult(1, 1));
//        classificationResults.add(new ClassificationResult(1, 4));
//
//        String begin = LocalDateTime.now().toString();
//        String end = LocalDateTime.now().toString();
//
//        IndividualTestResult result = new IndividualTestResult(
//                begin, end, features, history, classificationResults
//        );
//
//        SVMParams svmParams = new SVMParams()
//                .setGamma(1);
//
//        GAParams gaParams = new GAParams()
//                .setCr(0.1);
//
//        List<IndividualTestResult> list = new ArrayList<>();
//        list.add(result);
//        list.add(result);
//        list.add(result);
//
//        AggregateTestResult aggregate = new AggregateTestResult(gaParams, svmParams, list);
//
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            mapper.writeValue(new java.io.File("result.json"), aggregate);
//            AggregateTestResult test1 = mapper.readValue(
//                    new java.io.File("result.json"),
//                    AggregateTestResult.class
//            );
//
//            test1.getResults().get(1).addClassificationResult(22, 42);
//
//            mapper.writeValue(new java.io.File("2.json"), test1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}
