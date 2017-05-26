package com.reach.ekg.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import com.reach.ekg.server.View;
import spark.Request;
import spark.Response;

public class NewJob {

    private final View view = new View();

    private JobManager manager;

    public NewJob(JobManager manager) {
        this.manager = manager;
    }

    public Object handleNewJob(Request req, Response res) {
        try {
            String label = req.queryParams("label");
            int repeat = Integer.valueOf(req.queryParams("repeat"));

            // GA
            int generation = Integer.valueOf(req.queryParams("ga-generation"));
            int popSize = Integer.valueOf(req.queryParams("ga-pop"));
            double cr = Double.valueOf(req.queryParams("ga-cr"));
            double mr = Double.valueOf(req.queryParams("ga-mr"));
            GAParams gaParams = new GAParams()
                    .setGeneration(generation)
                    .setPopSize(popSize)
                    .setCr(cr)
                    .setMr(mr);

            // SVM
            double kernelParam = Double.valueOf(req.queryParams("svm-rbf-delta"));
            double gamma = Double.valueOf(req.queryParams("svm-gamma"));
            double lambda = Double.valueOf(req.queryParams("svm-lambda"));
            double c = Double.valueOf(req.queryParams("svm-c"));
            double epsilon = Double.valueOf(req.queryParams("svm-epsilon"));
            double maxIter = Double.valueOf(req.queryParams("svm-maxiter"));
            SVMParams svmParams = new SVMParams()
                    .setGamma(gamma)
                    .setC(c)
                    .setEpsilon(epsilon)
                    .setKernelParam(kernelParam)
                    .setMaxIter(maxIter)
                    .setLambda(lambda)
                    .setThreshold(0);

            manager.addJob(label, svmParams, gaParams, repeat);

            res.redirect("/history");
            return "Added successfully";

        } catch (NullPointerException e) {
            return view.template("_ekg-newtest")
                    .add("error", true)
                    .add("error_message", "Some parameteres are not set")
                    .render();

        } catch (NumberFormatException e) {
            return view.template("_ekg-newtest")
                    .add("error", true)
                    .add("error_message", "Parameters must be filled with numbers")
                    .render();
        }
    }

    public Object viewNewJobPage(Request req, Response res) {
        boolean disabled = manager.jobAvailable();
        View page = view.template("_ekg-newtest")
                .add("disabled", disabled);

        if (disabled) {
            page.add("error_message", "There is job currently running in the background");
        }

        return page.render();
    }
}
