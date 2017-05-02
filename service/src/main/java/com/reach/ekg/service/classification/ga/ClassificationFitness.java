package com.reach.ekg.service.classification.ga;

import com.reach.ekg.service.classification.data.DataSource;
import com.reach.ekg.service.classification.data.DataSources;
import com.reach.ekg.service.classification.svm.BDTSVM;
import com.reach.ekg.service.classification.svm.SVMFactory;

import static com.reach.ekg.service.util.IndexUtils.numOfTrue;

public class ClassificationFitness implements GA.FitnessFunction {

    @Override
    public double calculate(boolean[] genes) {
        // Classification
        DataSource training = DataSources.subFeatures(SVMFactory.training, genes);
        DataSource normalised = DataSources.subFeatures(SVMFactory.trainingNormalised, genes);
        DataSource test = DataSources.subFeatures(SVMFactory.testNormalised, genes);

        BDTSVM svm = SVMFactory.newInstance();
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
        double f2 = 1 - (double) numOfTrue(genes) / (double) genes.length;

        return 0.85 * f1 + 0.15 * f2;
    }
}
