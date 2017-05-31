package com.reach.ekg.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.service.test.AggregateTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FromFile {

    public static void start(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(new File(path));

            GAParams gaParams = mapper.readValue(json.get("gaParams").traverse(), GAParams.class);
            SVMParams svmParams = mapper.readValue(json.get("svmParams").traverse(), SVMParams.class);
            String label = json.get("label").asText();
            int repeat = json.get("repeat").asInt();

            AggregateTest test = new AggregateTest(label, svmParams, gaParams);
            test.run(repeat);

            AggregateTestResult result = test.getResult();
            String resultPath = "results/" + label + ".json";
            mapper.writeValue(new File(resultPath), result);
        } catch (IOException e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}
