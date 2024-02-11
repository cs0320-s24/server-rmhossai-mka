package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * Quick Summary:
 * Implements the Route interface, which requires defining a handle method to process incoming HTTP requests.
 * CSVParser object is initialized with a StrListCreatorFromRow object for deserializing CSV data into a list of strings.
 * handle method retrieves the file path of the CSV file to be loaded from the request query parameters.
 * FileReader is created for the CSV file using the file path.
 * New CSVParser object is created with a BufferedReader created from the FileReader.
 * New CSVParser object is returned as the response to the HTTP request.
 */

public class LoadCSVHandler implements Route {
    CSVParser<List<String>> parser;

    /**
     * Creates a new LoadCSVHandler instance with the given CSVParser.
     *
     * @param parser - the CSVParser to use for parsing CSV files.
     */
    public LoadCSVHandler(CSVParser parser){
        this.parser = parser;
    }

    /**
     * Handles a request to load a CSV file and returns a CSVParser object.
     *
     * @param request - the HTTP request object.
     * @param response - the HTTP response object.
     * @return - a CSVParser object.
     * @throws Exception - if there is any error loading the CSV file.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // gets the file path from the query parameters
        String csvFilePath = request.queryParams("loadCSV");
        // initializes a FileReader object for the CSV file
        FileReader reader = null;
        try {
             reader = new FileReader(csvFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("error");
        }
        // initializes a CSVParser object with the BufferedReader from the FileReader
        // and a StrListCreatorFromRow object
        parser = new CSVParser<>(new BufferedReader(reader),
                new StrListCreatorFromRow());
        // returns the CSVParser object
        return parser;
    }
}