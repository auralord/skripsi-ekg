package com.reach.ekg.service.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.ClassificationResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.classification.data.DataSources;
import com.reach.ekg.service.classification.data.Dataset;
import com.reach.ekg.service.classification.ga.GA;
import com.reach.ekg.service.classification.svm.BDTSVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.reach.ekg.service.Config.config;
import static com.reach.ekg.service.util.DateUtils.now;
import static com.reach.ekg.service.util.IndexUtils.numOfTrue;

public class IndividualTest {

    // Params
    private SVMParams svmParams;
    private GAParams gaParams;

    // Dataset
    private HashMap<Integer, List<Integer>> testData;
    private Dataset dataset;

    // Result
    private IndividualTestResult result;

    public IndividualTest(SVMParams svmParams, GAParams gaParams) {
        this.svmParams = svmParams;
        this.gaParams = gaParams;

        // Configure dataset
        dataset = new Dataset(DataSources.fromCSV(
                config.pathToCSV,
                config.delimiter,
                config.indexCol,
                config.classCol,
                config.dataColStart,
                config.dataLength));

        dataset.randomize();
    }

    public Dataset dataset() {
        return dataset;
    }

    public void run() {

        // Configure GA
        GA ga = new GA(gaParams);
        ga.generatePopulation(config.dataLength);
//        ga.setFitness(g -> 0);
        ga.setFitness(genes -> {
            int selected = numOfTrue(genes);
            if (selected <= 0) return 0;

            DataSource training =
                    DataSources.subFeatures(dataset.getTraining(), genes);
            DataSource normalised =
                    DataSources.subFeatures(dataset.getTrainingNormalised(), genes);
            DataSource test =
                    DataSources.subFeatures(dataset.getTestNormalised(), genes);

            BDTSVM svm = new BDTSVM(svmParams);
            svm.setTraining(training);
            svm.setTrainingNormalised(normalised);
            svm.train();

            int tests = test.count();
            int correct = 0;
            for (int i = 0; i < tests; i++) {
                double[] x = test.row(i);
                int y = svm.test(x);
                int y1 = test.target(i);
                if (y == y1) correct++;
            }

            double f1 = (double) correct / (double) tests;
            double f2 = 1 - (double) selected / (double) genes.length;
            double fitness = 0.85 * f1 + 0.15 * f2;
            System.out.printf("selected: %4s, f1: %.5f, fitness:%f\n", selected, f1, fitness);
            return fitness;
        });

        // Print data set info
        ObjectMapper mapper = new ObjectMapper();
        try {
            String asTest = mapper.writeValueAsString(dataset.getAsTest());
            System.out.println(asTest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // RUN!
        String begin = now();
        ga.run();
        String end = now();

        // Record available things
        List<Double> history = ga.getHistory();
        boolean[] features = ga.gBest().genes();
        double fitness = ga.gBest().fitness();

        // Classification for one last time (TM)
        List<ClassificationResult> cResults = new ArrayList<>();
        DataSource training =
                DataSources.subFeatures(dataset.getTraining(), features);
        DataSource normalised =
                DataSources.subFeatures(dataset.getTrainingNormalised(), features);
        DataSource test =
                DataSources.subFeatures(dataset.getTestNormalised(), features);

        BDTSVM svm = new BDTSVM(svmParams);
        svm.setTraining(training);
        svm.setTrainingNormalised(normalised);
        svm.train();

        int tests = test.count();
        int correct = 0;
        for (int i = 0; i < tests; i++) {
            double[] x = test.row(i);
            int y = svm.test(x);
            int y1 = test.target(i);
            if (y == y1) correct++;
            cResults.add(new ClassificationResult(y, y1));
        }

        double accuracy = (double) correct / (double) tests;
        double featurePercentage = 1 - (double) numOfTrue(features) / (double) features.length;

        // :shrug:
        result = new IndividualTestResult();
        result.setBegin(begin);
        result.setEnd(end);
        result.setHistory(history);
        result.setFeatures(features);
        result.setClassificationResults(cResults);
        result.setAccuracy(accuracy);
        result.setFeaturesPercentage(featurePercentage);
        result.setFitness(fitness);
    }

    public IndividualTestResult getResult() {
        return result;
    }
}
