package edu.brown.cs.student.main.server;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class SearchCSVHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        return null;
    }

    public record CSVSuccessResponse(String response_type,
                                     Map<String, Object> responseMap) {

    }

    public record CSVFailureResponse(String response_type,
                                     Map<String, Object> responseMap) {


    }
}
