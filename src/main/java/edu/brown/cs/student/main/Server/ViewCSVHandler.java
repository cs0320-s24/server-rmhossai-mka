package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.DataSource.DatasourceException;
import edu.brown.cs.student.main.Parser.CSVParser;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.xml.crypto.Data;
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
     * @param request - the HTTP request.
     * @param response - the HTTP response.
     * @return - serialized response containing CSV data.
     * @throws Exception - if there is an error handling the request.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // check if CSVDataSource is initialized
        if (source.getCurrentMatrix() == null) {
            // if not initialized, throw an exception
            throw new DatasourceException("No data source initialized");
        }
        // create a map to hold CSV data
        Map<String, Object> resMap = new HashMap<>();
        // put CSV data into the map
        resMap.put("csvData", source.getCurrentMatrix());
        // print CSV data to console
        System.out.println(source.getCurrentMatrix());
        // return serialized response
        return new CSVSuccessResponse(resMap).serialize();
    }

    /**
     * Represents a successful response to a CSV request.
     *
     * @param response_type - the type of the response.
     * @param responseMap - the response map containing CSV data.
     */
    public record CSVSuccessResponse(String response_type, Map<String, Object> responseMap) {

        /**
         * Constructs a CSVSuccessResponse with the given response map.
         *
         * @param responseMap - the response map containing CSV data.
         */
        public CSVSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
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
                JsonAdapter<CSVSuccessResponse> adapter =
                        moshi.adapter(CSVSuccessResponse.class);
                // serialize the response object to JSON
                return adapter.toJson(this).replace("\\\"", "\"");
            } catch (Exception e) {
                // for debugging purposes, show in the console _why_ this fails
                // otherwise, we'll just get an error 500 from the API in integration testing
                // print stack trace if the serialization fails
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Represents a failure response to a CSV request.
     * 
     * @param response_type - the type of the response.
     * @param responseMap - the response map containing CSV data.
     */
    public record CSVFailureResponse(String response_type, Map<String, Object> responseMap) {

        /**
         * Serializes the CSVFailureResponse to JSON.
         *
         * @return - JSON representation of the response.
         */
        String serialize() {
            // initialize Moshi to serialize the response to JSON
            Moshi moshi = new Moshi.Builder().build();
            // serialize the response object to JSON
            return moshi.adapter(CSVFailureResponse.class).toJson(this).replace("\\\"", "\"");
        }
    }
}