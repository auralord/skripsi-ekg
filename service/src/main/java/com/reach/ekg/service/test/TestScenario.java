package com.reach.ekg.service.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.service.ga.GAParams;
import com.reach.ekg.service.svm.SVMParams;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TestScenario {

    // Params
    public SVMParams svmParams;
    public GAParams gaParams;

    // Results
    public boolean[] features;
    public double[] history;

    public static void main(String[] args) throws IOException {
        TestScenario scenario = new TestScenario();
        scenario.svmParams = new SVMParams()
                .setC(1)
                .setEpsilon(0.11);
        scenario.gaParams = new GAParams()
                .setGeneration(12)
                .setMr(0.1);
        scenario.features = com.reach.ekg.service.util.RandomUtil.randomBoolean(10);
        scenario.history = new double[] {1, 2, 3, 4, 5};

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("lol.json"), scenario);

        System.out.println();
        TestScenario scenario2 = mapper.readValue(new File("lol.json"), TestScenario.class);
        String lul = Arrays.toString(scenario2.features);
        System.out.println(scenario2.gaParams);
    }
}
