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
import com.reach.ekg.service.classification.ga.Chromosome;
import com.reach.ekg.service.classification.ga.GA;
import com.reach.ekg.service.classification.ga.operators.BinaryOperators;
import com.reach.ekg.service.classification.ga.operators.RealOperators;
import com.reach.ekg.service.classification.svm.BDTSVM;
import com.reach.ekg.service.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.reach.ekg.service.Config.config;
import static com.reach.ekg.service.util.DateUtils.now;
import static com.reach.ekg.service.util.IndexUtils.numOfTrue;

public class IndividualTest {

    // Params' params
    private final int PARAMS = 5;
    private final double[] MAX_BOUND = {5.0, 100.0, 2.0, 50.0, 1.0};
    private final double[] MIN_BOUND = {0.0, 0.01, 10e-7, 10e-3, 10e-7};
    private final int KERNEL_PARAMS = 0;
    private final int LAMBDA = 1;
    private final int GAMMA = 2;
    private final int C = 3;
    private final int EPSILON = 4;

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

        // Print data set info
        ObjectMapper mapper = new ObjectMapper();
        try {
            String asTest = mapper.writeValueAsString(dataset.getAsTest());
            System.out.println(asTest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        /*
         *
         * Begin GA phase 1 for feature selection
         *
         * */

        // Configure GA for feature selection
        GA ga = new GA(gaParams);
        ga.setCrossover(BinaryOperators::oneCutPoint);
        ga.setMutation(BinaryOperators::singleMutation);

        ga.generatePopulation(config.dataLength, i -> {
            boolean[] genes = RandomUtil.rand(i);
            return new Chromosome<>(genes);
        });

//        ga.setFitness(c -> 0);
        ga.setFitness(chromosome -> {
            boolean[] genes = (boolean[]) chromosome.genes();

            int selected = numOfTrue(genes);
            if (selected <= 0) return 0;

            DataSource training = DataSources.subFeatures(dataset.getTraining(), genes);
            DataSource normalised = DataSources.subFeatures(dataset.getTrainingNormalised(), genes);
            DataSource test = DataSources.subFeatures(dataset.getTestNormalised(), genes);

            BDTSVM model = new BDTSVM(svmParams);
            model.setTraining(training);
            model.setTrainingNormalised(normalised);
            model.train();

            int tests = test.count();
            int correct = 0;
            for (int i = 0; i < tests; i++) {
                double[] x = test.row(i);
                int y = model.test(x);
                int t = test.target(i);
                if (y == t) correct++;
            }

            double f1 = (double) correct / (double) tests;
            double f2 = 1 - (double) selected / (double) genes.length;
            double fitness = 0.85 * f1 + 0.15 * f2;
//            System.out.printf("selected: %4s, f1: %.5f, fitness:%f\n", selected, f1, fitness);
            return fitness;
        });

        // Run GA for feature selection
        String begin = now();
        ga.run();

        // Generate data for next phase
        boolean[] features = (boolean[]) ga.gBest().genes();
        DataSource training = DataSources.subFeatures(dataset.getTraining(), features);
        DataSource normalised = DataSources.subFeatures(dataset.getTrainingNormalised(), features);
        DataSource test = DataSources.subFeatures(dataset.getTestNormalised(), features);

        /*
        *
        * Begin GA phase 2 for parameter optimization
        *
        * */

        // Configure GA for parameter optimization
        RealOperators rcga = new RealOperators(MAX_BOUND, MIN_BOUND);

        GA ga2 = new GA(gaParams.setGeneration(100));
        ga2.setCrossover(rcga::crossover);
        ga2.setMutation(rcga::mutation);

        Random r = RandomUtil.r();
        ga2.generatePopulation(2, index -> {
            double[] genes = new double[PARAMS];
            for (int i = 0; i < PARAMS; i++) {
                genes[i] = MIN_BOUND[i] + r.nextGaussian() * (MAX_BOUND[i] - MIN_BOUND[i]);
            }
            return new Chromosome<>(genes);
        });

//        ga2.setFitness(c -> {
//            double x = ((double[]) c.genes())[1];
//
//            return x * x - 3 * x;
//        });
        ga2.setFitness(chromosome -> {
            double[] genes = (double[]) chromosome.genes();
            SVMParams newParams = toSVMParams(genes);

            BDTSVM model = new BDTSVM(newParams);
            model.setTraining(training);
            model.setTrainingNormalised(normalised);
            model.train();

            int tests = test.count();
            int correct = 0;
            for (int i = 0; i < tests; i++) {
                double[] x = test.row(i);
                int y = model.test(x);
                int t = test.target(i);
                if (y == t) correct++;
            }

//            try {
//                System.out.println(mapper.writeValueAsString(newParams));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }

//            System.out.println("Akurasi: " + (double) correct / (double) tests);
            return (double) correct / (double) tests;
        });

        // Run GA for parameter optimization
        ga2.run();
        String end = now();

        // Record available things
        List<Double> history = ga.getHistory();
        double fitness = ga.gBest().fitness();

        // Classification for one last time (TM)
        List<ClassificationResult> cResults = new ArrayList<>();

        SVMParams newParams = toSVMParams((double[]) ga2.gBest().genes());
        BDTSVM model = new BDTSVM(newParams);
        model.setTraining(training);
        model.setTrainingNormalised(normalised);
        model.train();

        int tests = test.count();
        int correct = 0;
        for (int i = 0; i < tests; i++) {
            double[] x = test.row(i);
            int y = model.test(x);
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

        System.out.println("Akurasi akhir: " + (accuracy * 100) + "%");
        System.out.println("Parameter SVM: " + newParams);
    }

    public IndividualTestResult getResult() {
        return result;
    }

    private SVMParams toSVMParams(double[] genes) {
        return new SVMParams()
                .setKernelParam(genes[KERNEL_PARAMS])
                .setLambda(genes[LAMBDA])
                .setGamma(genes[GAMMA])
                .setC(genes[C])
                .setEpsilon(genes[EPSILON])
                .setThreshold(0)
                .setMaxIter(100);

    }
}
