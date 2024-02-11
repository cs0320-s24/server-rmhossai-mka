package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import java.util.Map;

public class LoadCSVHandler implements Route {

    CSVParser<List<String>> parser;

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String csvFilePath = request.queryParams("soupName");

        return null;
    }

}
