package com.reach.ekg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.Message;
import com.reach.ekg.persistence.State;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.service.test.AggregateTest;
import org.apache.http.client.fluent.Request;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.reach.ekg.service.Config.config;
import static com.reach.ekg.service.util.DateUtils.now;

public class Main {

    private static final String STATUS = "/service/status";
    private static final String START = "/service/start";
    private static final String UPDATE = "/service/update";
    private static final String FINISH = "/service/finish";
    private static final String RESET = "/service/reset";

    private static ObjectMapper mapper;
    private static String server;

    public static void main(String[] args) throws IOException {
        // Read config from file
        mapper = new ObjectMapper();
        config = mapper.readValue(new File("config.json"), Config.class);
        server = "http://" + config.hostname + ":" + config.port;

        String status;
        int i = 0;
        do {
            JsonNode tree = mapper.readTree(new URL(server + STATUS));
            status = tree.get(Message.STATE).asText("unknown");

            switch (status) {
                case State.STARTED:
                    Request.Post(server + START).execute();
                    startJob(tree.get(Message.JOB));
                    Request.Post(server + FINISH).execute();
                    break;

                case State.FINISHED:
                    Request.Post(server + RESET).execute();
                    System.out.println(status);
                    break;

                default:
                    System.out.println(status);
            }

            sleep(1000);
        } while (!status.equals(State.STOP_SERVICE));
    }

    private static void startJob(JsonNode node) {
        try {
            // Read node
            String svmJson = node.get(Message.SVM_PARAMS).toString();
            String gaJson = node.get(Message.GA_PARAMS).toString();

            String label = node.get(Message.LABEL).asText();
            int repeat = node.get(Message.REPEAT).asInt();
            SVMParams svmParams = mapper.readValue(svmJson, SVMParams.class);
            GAParams gaParams = mapper.readValue(gaJson, GAParams.class);

            // Start test
            AggregateTest test = new AggregateTest(label, svmParams, gaParams);
            test.postIteration(() -> {
                try {
                    Request.Post(server + UPDATE).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println(now() + ": Test started");
            test.run(repeat);

            // Write result
            AggregateTestResult result = test.getResult();
            mapper.writeValue(new File("result.json"), result);

        } catch (NullPointerException | IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}