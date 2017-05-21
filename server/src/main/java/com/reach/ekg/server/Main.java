package com.reach.ekg.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.server.controllers.History;
import com.reach.ekg.server.controllers.JobManager;
import spark.Spark;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Setup Jackson
        ObjectMapper mapper = new ObjectMapper();

        // Read config
        int port;
        try {
            JsonNode config = mapper.readTree(new File("config.json"));
            port = config.get("port").asInt();
        } catch (IOException e) {
            port = 4567;
            System.out.println("No config.json detected. Using default config");
        }

        // Setup server
        Spark.port(port);
        Spark.staticFileLocation("public");

        // Endpoints for service communication
        JobManager manager = new JobManager();
        Spark.path("/service", () -> {
            Spark.get("/status",    manager::status,    mapper::writeValueAsString);
            Spark.post("/start",    manager::start,     mapper::writeValueAsString);
            Spark.post("/update",   manager::update,    mapper::writeValueAsString);
            Spark.post("/finish",   manager::finish,    mapper::writeValueAsString);
            Spark.post("/reset",    manager::reset,     mapper::writeValueAsString);

            Spark.afterAfter("/*",  (req, res) -> {
                if (res.status() != 404) {
                    res.type("application/json");
                }
            });
        });

        // User-accessible URLs
        History history = new History(manager);
        Spark.get("/thyme", history::viewHistory);

        // Only for testing
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
