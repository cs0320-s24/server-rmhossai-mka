package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quick Summary:
 * Handles HTTP requests to view CSV data.
 * Checks if the CSV data source is initialized and retrieves the current matrix.
 * Constructs a response map containing the CSV data.
 * Serializes the response map to JSON using Moshi.
 * Provides the serialized JSON response as the HTTP response.
 * Contains inner record classes for representing success and failure responses.
 */
public class ViewCSVHandler implements Route  {

    private CSVDataSource source;

    /**
     * Constructs a ViewCSVHandler with the given CSVDataSource.
     *
     * @param source - the CSV data source.
     */
    public ViewCSVHandler(CSVDataSource source){
        this.source = source;
    }

    /**
     * Handles the HTTP request to view CSV data.
     *
     * @param result - the HTTP request.
     * @param data - the HTTP response.
     * @return - serialized response containing CSV data.
     * @throws Exception - if there is an error handling the request.
     */
    @Override
    public Object handle(Request result, Response data) throws Exception {
        // check if CSVDataSource is initialized
        if (source.getCurrentMatrix() == null) {
            // if not initialized, return a failure response
            return new CSVFailureResponse("error",
                    "No data source initialized").serialize();
        }

        // return serialized response with response matrix
        return new CSVSuccessResponse(source.getCurrentMatrix().mtrx()).serialize();
    }

    /**
     * Represents a successful response to a CSV request.
     *
     * @param result - the type of the response.
     * @param data - the response list containing CSV data.
     */
    public record CSVSuccessResponse(String result, List<List<String>> data) {

        /**
         * Constructs a CSVSuccessResponse with the given response.
         *
         * @param data - the response list containing CSV search results.
         */
        public CSVSuccessResponse(List<List<String>> data) {
            this("success", data);
        }

        /**
         * Serializes the CSVSuccessResponse to JSON.
         *
         * @return - JSON representation of the response.
         */
        String serialize() {
            try {
                // initialize Moshi to serialize the response to JSON
                Moshi moshi = new Moshi.Builder().build();
                // create a JSON adapter for CSVSuccessResponse
                JsonAdapter<CSVSuccessResponse> adapter = moshi.adapter(CSVSuccessResponse.class);
                // serialize the response object to JSON
                return adapter.toJson(this).replace("\\\"", "\"");
            } catch (Exception e) {
                // for debugging purposes, show in the console _why_ this fails
                // otherwise, we'll just get an error 500 from the API in integration testing
                // print the stack trace if serialization fails
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Represents a failure response to a CSV search request.
     *
     * @param result - the type of the response.
     * @param msg - the resulting error message.
     */
    public record CSVFailureResponse(String result, String msg) {

        /**
         * Serializes the CSVFailureResponse to JSON.
         *
         * @return - JSON representation of the response.
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(CSVFailureResponse.class).toJson(this)
                    .replace("\\\"", "\"");
        }
    }
}