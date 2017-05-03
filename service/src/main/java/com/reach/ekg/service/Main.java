package com.reach.ekg.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.service.test.AggregateTest;

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

    }
}