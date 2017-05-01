package com.reach.ekg.service.ga;

import com.reach.ekg.service.data.DataSource;
import com.reach.ekg.service.svm.BDTSVM;
import com.reach.ekg.service.svm.SVMFactory;

public class ClassificationFitness implements GA.FitnessFunction {

    @Override
    public double calculate(boolean[] genes) {
        // Classification
        DataSource training = SVMFactory.training;
        DataSource normalised = SVMFactory.trainingNormalised;
        DataSource test = SVMFactory.testNormalised;

//        java.util.Date d1 = new java.util.Date();
//        DataSource training = DataSources.subFeatures(SVMFactory.training, genes);
//        DataSource normalised = DataSources.subFeatures(SVMFactory.trainingNormalised, genes);
//        DataSource test = DataSources.subFeatures(SVMFactory.testNormalised, genes);
//        java.util.Date d2 = new java.util.Date();
//        System.out.println("forming subfeature: " + (d2.getTime() - d1.getTime()));

        BDTSVM svm = SVMFactory.newInstance();
        svm.setTraining(training);
        svm.setTrainingNormalised(normalised);
        svm.train();

        int tests = test.count();
        int correct = 0;
        for (int i = 0; i < tests; i++) {
//            java.util.Date d3 = new java.util.Date();
            double[] x = test.row(i);
            int y = svm.test(x);
//            java.util.Date d4 = new java.util.Date();
//            System.out.println("testing: " + (d4.getTime() - d3.getTime()));
            int y1 = test.target(i);

            System.out.println(y + " " + y1);

            if (y == y1) correct++;
        }

        double f1 = (double) correct / (double) tests;

        return f1;
    }
}
