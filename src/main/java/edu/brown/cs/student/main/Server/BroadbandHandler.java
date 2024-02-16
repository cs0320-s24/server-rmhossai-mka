package edu.brown.cs.student.main.Server;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.student.main.DataSource.ACSDatasource;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Quick Summary:
 * Handles HTTP requests to retrieve broadband percentage data for a given state and county.
 * Utilizes an ACS datasource to fetch broadband percentage from ACS API.
 * Serializes response data to JSON format.
 */

/**
 * Handles HTTP requests for broadband percentage data.
 */
public class BroadbandHandler implements Route {
    public ACSDatasource acsDatasource;

    /**
     * Constructs a BroadbandHandler with the specified ACS datasource.
     *
     * @param acsDatasource - the ACS datasource to be used for fetching broadband percentage data.
     */
    public BroadbandHandler(ACSDatasource acsDatasource){
        this.acsDatasource = acsDatasource;
    }

    /**
     * Handles HTTP requests and returns broadband percentage data as JSON.
     *
     * @param request - the HTTP request object.
     * @param response - the HTTP response object.
     * @return - JSON representation of broadband percentage data.
     * @throws Exception - if an error occurs while processing the request.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // get state and county parameters
        String state = request.queryParams("state");
        String county = request.queryParams("county");
        // make request to ACS API to get broadband percentage
        double broadbandPercentage = acsDatasource.getBroadbandPercentage(state, county);
        // construct the response map
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("state", state);
        responseMap.put("county", county);
        responseMap.put("broadbandPercentage", broadbandPercentage);
        // serialize response to JSON and return
        return new BroadbandSuccessResponse(responseMap).serialize();
    }

    /**
     * Represents a successful response to a broadband percentage request.
     *
     * @param response_type - the type of the response.
     * @param responseMap - the response map containing CSV search results.
     */
    public record BroadbandSuccessResponse(String response_type, Map<String, Object> responseMap) {

        /**
         * Constructs a BroadbandSuccessResponse with the given response.
         *
         * @param responseMap - the response map containing CSV search results.
         */
        public BroadbandSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }

        /**
         * Serializes the BroadbandSuccessResponse to JSON.
         *
         * @return - JSON representation of the response.
         */
        String serialize() {
            try {
                // initialize Moshi to serialize the response to JSON
                Moshi moshi = new Moshi.Builder().build();
                // create a JSON adapter for CSVSuccessResponse
                JsonAdapter<BroadbandSuccessResponse> adapter = moshi.adapter(
                    BroadbandSuccessResponse.class);
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
     * @param response_type - the type of the response.
     * @param responseMap - the response map containing CSV search results.
     */
    public record BroadbandFailureResponse(String response_type, Map<String, Object> responseMap) {

        /**
         * Serializes the BroadbandFailureResponse to JSON.
         *
         * @return - JSON representation of the response.
         */
        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(BroadbandFailureResponse.class).toJson(this)
                .replace("\\\"", "\"");
        }
    }
}