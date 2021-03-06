package com.reach.ekg.server.controllers;

import com.reach.ekg.persistence.Message;
import com.reach.ekg.persistence.State;
import com.reach.ekg.persistence.params.GAParams;
import com.reach.ekg.persistence.params.SVMParams;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class JobManager {

    public static class Job {
        private String label;
        private SVMParams svmParams;
        private GAParams gaParams;
        private int repeat;

        Job(String label, SVMParams svmParams, GAParams gaParams, int repeat) {
            this.label = label;
            this.svmParams = svmParams;
            this.gaParams = gaParams;
            this.repeat = repeat;
        }

        public String getLabel() {
            return label;
        }

        public SVMParams getSvmParams() {
            return svmParams;
        }

        public GAParams getGaParams() {
            return gaParams;
        }

        public int getRepeat() {
            return repeat;
        }
    }

    private Job job;
    private String state;
    private int completed;

    public JobManager() {
        this.state = State.IDLE;
    }

    public boolean addJob(String label, SVMParams svmParams, GAParams gaParams, int repeat) {
        if (job == null) {
            job = new Job(label, svmParams, gaParams, repeat);
            completed = 0;
            state = State.STARTED;
            return true;
        } else {
            return false;
        }
    }

    public boolean jobAvailable() {
        return job != null;
    }

    public Job getJob() {
        return job;
    }

    private HashMap<String, Object> map(String k, Object v) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    private HashMap<String, Object> stateMap() {
        return map(Message.STATE, state);
    }

    private Object error(Response res) {
        res.status(500);
        return map(Message.ERROR, "No job available");
    }

    /*
     * Methods to handle service requests
     */
    public Object status(Request req, Response res) {
        HashMap<String, Object> message = stateMap();

        switch (state) {
            case State.STARTED:
                message.put(Message.JOB, job);
                break;
            case State.WORKING:
                message.put(Message.JOB, job);
                message.put(Message.COMPLETED, completed);
                break;
        }

        return message;
    }

    public Object start(Request req, Response res) {
        if (state.equals(State.STARTED) && job != null) {
            state = State.WORKING;
            return stateMap();
        } else {
            return error(res);
        }
    }

    public Object update(Request req, Response res) {
        if (state.equals(State.WORKING) && job != null) {
            completed++;
            return stateMap();
        } else {
            return error(res);
        }
    }

    public Object finish(Request req, Response res) {
        if (state.equals(State.WORKING) && job != null) {
            state = State.FINISHED;
            return stateMap();
        } else {
            return error(res);
        }
    }

    public Object reset(Request req, Response res) {
        state = State.IDLE;
        job = null;
        return stateMap();
    }
}
