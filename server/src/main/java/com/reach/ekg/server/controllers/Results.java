package com.reach.ekg.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.results.AggregateTestResult;
import com.reach.ekg.persistence.results.ClassificationResult;
import com.reach.ekg.persistence.results.IndividualTestResult;
import com.reach.ekg.server.View;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.reach.ekg.server.Names.NAMES;
import static spark.Spark.notFound;

public class Results {

    public static class ResultEntry {
        public double accuracy;
        public String numCorrect;
        public String colorClass;

        public ResultEntry() {}

        public ResultEntry(double accuracy, String numCorrect) {
            this.accuracy = accuracy;
            this.numCorrect = numCorrect;

            this.colorClass = "danger";
            if (accuracy >= 50) this.colorClass = "warning";
            if (accuracy >= 70) this.colorClass = "info";
            if (accuracy >= 90) this.colorClass = "success";
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final View view = new View();

    private String resultID;
    private AggregateTestResult results;
    private List<ResultEntry> entries;
    private IndividualTestResult details;

    private List<ResultEntry> testResultsToEntries(AggregateTestResult results) {
        return results.getResults().stream().map(r -> {
            double accuracy = r.getAccuracy() * 100;
            int numCorrect = (int) r.getClassificationResults().stream()
                    .filter(ClassificationResult::correct)
                    .count();
            int total = r.getClassificationResults().size();
            return new ResultEntry(accuracy, numCorrect + " / "+ total + " correct");
        }).collect(Collectors.toList());
    }

    public Object viewTestDetails(Request req, Response res) {
        String newID = req.params("id");
        int testNum;

        if(newID == null) {
            // TODO add error page
            notFound("not found");
            return "not found";
        }

        if(!newID.equals(resultID)) {
            try {
                String path = "results/" + newID + ".json";
                results = mapper.readValue(new File(path), AggregateTestResult.class);
                entries = testResultsToEntries(results);

                resultID = newID;

                System.out.println(resultID);
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

        return view.template("_ekg-individual")
                .add("id", newID)
                .add("results", results)
                .add("entries", entries)
                .add("active", testNum)
                .add("details", details)
                .add("names", NAMES)
                .render();
    }
}
