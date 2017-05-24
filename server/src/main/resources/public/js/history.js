$(document).ready(function () {
    function getURL() {
        var protocol = window.location.protocol;
        var hostname = window.location.hostname;
        var port = window.location.port;
        var path = "/service/status";
        return protocol + "//" + hostname + ":" + port + path;
    }

    function sendXHR() {
        var url = getURL();
        var xhr = new XMLHttpRequest();
        xhr.open("GET", url);
        xhr.onload = function () {
            var json = JSON.parse(xhr.responseText);
            switch (json.state) {
                case "IDLE":
                    $("#jobRunning").hide();
                    $("#jobNone").show();
                    break;

                case "STARTED":
                    updateFields(json);
                    $("#jobProgress").text("Waiting for service to start...");
                    $("#progressBar").css("width", "100%");
                    break;

                case "WORKING":
                    updateFields(json);
                    var completed = json.completed;
                    var total = json.job.repeat;
                    if (completed == null || completed == undefined) completed = 0;
                    var percent = completed / total * 100;

                    var textElapsed = completed + " / " + total + " completed";
                    $("#jobProgress").text(textElapsed);
                    $("#progressBar").css("width", percent + "%");
                    break;
            }
        }
        xhr.send();
    }

    function updateFields(json) {
        $("#jobRunning").show();
        $("#jobNone").hide();

        var ga = json.job.gaParams;
        $("#gaPopSize").text(ga.popSize);
        $("#gaGeneration").text(ga.generation);
        $("#gaCr").text(ga.cr);
        $("#gaMr").text(ga.mr);

        var svm = json.job.svmParams;
        $("#svmEpsilon").text(svm.epsilon);
        $("#svmC").text(svm.C);
        $("#svmGamma").text(svm.gamma);
        $("#svmLambda").text(svm.lambda);
        $("#svmKernelParam").text(svm.kernelParam);
        $("#svmMaxiter").text(svm.maxIter);

        $("#jobTitle").text(json.job.label);
    }

    $("#jobRunning").hide();
    setInterval(sendXHR, 1000);
});
