package com.reach.ekg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.service.test.AggregateTest;
import com.reach.ekg.service.util.FileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FromFile {

    public static void start(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory())
                readDir(path);
            else
                readFile(path);
        } else {
            System.out.println(path + " do not exits.");
        }

    }

    private static void readDir(String path) {
        try {
            List<String> files = new ArrayList<>();
            Files.newDirectoryStream(Paths.get(path), p -> p.toString().endsWith(".json"))
                    .forEach(p -> files.add(p.toString()));

            files.forEach(FromFile::readFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFile(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(new File(path));

            GAParams gaParams = mapper.readValue(json.get("gaParams").traverse(), GAParams.class);
            SVMParams svmParams = mapper.readValue(json.get("svmParams").traverse(), SVMParams.class);
            String label = json.get("label").asText();
            int repeat = json.get("repeat").asInt();

            System.out.println("RUNNING TEST: " + label);
            AggregateTest test = new AggregateTest(label, svmParams, gaParams);
            test.run(repeat);

            String resultPath = "results/" + label + ".json";
            AggregateTestResult result = test.getResult();
            FileWriter.writeResult(resultPath, result);
        } catch (IOException e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}
