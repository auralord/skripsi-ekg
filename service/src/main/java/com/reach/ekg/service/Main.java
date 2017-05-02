package com.reach.ekg.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.service.test.IndividualTest;

import java.io.File;
import java.io.IOException;

import static com.reach.ekg.service.Config.config;

public class Main {

    public static void main(String[] args) throws IOException {
        // Configure Jackson parser
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        // Read config from file
        config = mapper.readValue(new File("config.json"), Config.class);
        System.out.println(config);

        // LUL
        GAParams gaParams = new GAParams()
                .setCr(0.9)
                .setMr(0.1)
                .setGeneration(10)
                .setPopSize(10);
        SVMParams svmParams = new SVMParams()
                .setLambda(0.5)
                .setGamma(0.01)
                .setC(1)
                .setEpsilon(0.00001)
                .setThreshold(0)
                .setMaxIter(100)
                .setKernelParam(2);

        IndividualTest test = new IndividualTest(svmParams, gaParams);
        test.run();
        System.out.println(test.getResult().getAccuracy());


    }
}