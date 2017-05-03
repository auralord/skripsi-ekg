package com.reach.ekg.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.server.controllers.JobManager;
import spark.Spark;

import java.io.File;
import java.io.IOException;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;

public class Main {

    public static void main(String[] args) throws IOException {
        // Setup Jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(ALLOW_COMMENTS, true);

        // Read config
        JsonNode config = mapper.readTree(new File("config.json"));
        int port = config.get("port").asInt();

        // Setup server
        Spark.port(port);
        Spark.staticFileLocation("public");

        // Endpoints for service communication
        JobManager manager = new JobManager(mapper);
        Spark.path("/service", () -> {
            Spark.before((req, res) -> res.header("Content-Type", "application/json"));
            Spark.get("/status", manager::status);
            Spark.post("/start", manager::start);
            Spark.post("/finish", manager::finish);
            Spark.post("/stop", manager::stop);
            Spark.post("/reset", manager::reset);
        });

        // User-accessible URLs
        Spark.get("/add-job/:name/:repeat", (req, res) -> {
            try {
                String label = req.params("name");
                int repeat = Integer.valueOf(req.params("repeat"));
                SVMParams svmParams = new SVMParams()
                        .setLambda(0.5)
                        .setGamma(0.01)
                        .setC(1)
                        .setEpsilon(0.00001)
                        .setThreshold(0)
                        .setMaxIter(100)
                        .setKernelParam(2);

                GAParams gaParams = new GAParams()
                        .setCr(0.9)
                        .setMr(0.1)
                        .setGeneration(10)
                        .setPopSize(10);

                boolean success = manager.addJob(label, svmParams, gaParams, repeat);
                if (success) {
                    return "Job added";
                } else {
                    return "Can't add job";
                }
            } catch (NumberFormatException e) {
                System.err.println("ERROR: " + e.getMessage());
                return "Must be number";
            }
        });
    }
}
