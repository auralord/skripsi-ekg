package com.reach.ekg.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.server.View;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static com.reach.ekg.server.Names.NAMES;
import static spark.Spark.notFound;

public class Results {

    public static class ResultEntry {
        public double fitness;
        public String colorClass;

        public ResultEntry() {
        }

        public ResultEntry(double fitness) {
            this.fitness = fitness;

            this.colorClass = "danger";
            if (fitness >= 0.50) this.colorClass = "warning";
            if (fitness >= 0.65) this.colorClass = "info";
            if (fitness >= 0.80) this.colorClass = "success";
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final View view = new View();

    private String resultID;
    private AggregateTestResult results;
    private List<ResultEntry> entries;
    private IndividualTestResult details;

    private List<ResultEntry> testResultsToEntries(AggregateTestResult results) {
        return results.getResults().stream()
                .map(r -> new ResultEntry(r.getFitness()))
                .collect(Collectors.toList());
    }

    private int trueCount(boolean[] booleans) {
        int i = 0;
        for (boolean b : booleans) if (b) i++;
        return i;
    }

    public Object viewTestDetails(Request req, Response res) {
        String newID = req.params("id");
        int testNum;

        if (newID == null) {
            // TODO add error page
            notFound("not found");
            return "not found";
        }

        if (!newID.equals(resultID)) {
            try {
                String path = "results/" + newID + ".json";
                results = mapper.readValue(new File(path), AggregateTestResult.class);
                entries = testResultsToEntries(results);

                resultID = newID;
            } catch (IOException e) {
                notFound(e.getMessage());
                return e.getMessage();
            }
        }

        try {
            testNum = Integer.parseInt(req.params("testNum"));
            details = results.getResults().get(testNum - 1);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            notFound(e.getMessage());
            return e.getMessage();
        }

        double selectedFeatures = trueCount(details.getFeatures());

        double avgFitness = 0;
        OptionalDouble opt = results.getResults().stream()
                .mapToDouble(IndividualTestResult::getFitness)
                .average();
        if (opt.isPresent()) avgFitness = opt.getAsDouble();

        return view.template("_ekg-individual")
                .add("id", newID)
                .add("results", results)
                .add("entries", entries)
                .add("details", details)
                .add("active", testNum)
                .add("avgFitness", avgFitness)
                .add("selectedFeatures", selectedFeatures)
                .add("names", NAMES)
                .render();
    }

    public Object getFitnessHistoryCSV(Request req, Response res) {
        res.type("text/csv");

        int i = 1;
        StringBuilder sb = new StringBuilder("No,Fitness\n");
        for (double e : details.getHistory()) {
            sb.append(i).append(",").append(e).append("\n");
            i++;
        }

        return sb.toString();
    }
}
