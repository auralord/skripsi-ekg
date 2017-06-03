package com.reach.ekg.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.server.controllers.History;
import com.reach.ekg.server.controllers.JobManager;
import com.reach.ekg.server.controllers.NewJob;
import com.reach.ekg.server.controllers.Results;
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
            Spark.get("/status", manager::status, mapper::writeValueAsString);
            Spark.post("/start", manager::start, mapper::writeValueAsString);
            Spark.post("/update", manager::update, mapper::writeValueAsString);
            Spark.post("/finish", manager::finish, mapper::writeValueAsString);
            Spark.post("/reset", manager::reset, mapper::writeValueAsString);

            Spark.afterAfter("/*", (req, res) -> {
                if (res.status() != 404) {
                    res.type("application/json");
                }
            });
        });

        // User-accessible URLs
        History history = new History();
        Spark.get("/", history::viewHistory);
        Spark.get("/history", history::viewHistory);

        NewJob newJob = new NewJob(manager);
        Spark.get("/new-test", newJob::viewNewJobPage);
        Spark.post("/new-test", newJob::handleNewJob);

        Results results = new Results();
        Spark.get("results/:id/:testNum", results::viewTestDetails);
        Spark.get("results/:id/:testNum/history-csv", results::getFitnessHistoryCSV);
    }
}
