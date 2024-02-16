package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Constants.Errors;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Exceptions.ColumnConversionException;
import edu.brown.cs.student.main.ParserUtils.UtilitySearch;
import edu.brown.cs.student.main.ParserUtils.UtilitySearch.Options;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Quick Summary:
 * Handles HTTP requests to search within CSV data.
 * Retrieves the current CSV matrix from the data source.
 * Parses request parameters to determine search options and criteria.
 * Performs search operations on the CSV data matrix.
 * Constructs and returns success or failure responses based on search results.
 */
public class SearchCSVHandler implements Route {

    private CSVDataSource source;

    /**
     * Constructs a SearchCSVHandler with the given CSVDataSource.
     *
     * @param source - the CSV data source.
     */
    public SearchCSVHandler(CSVDataSource source){
        this.source = source;
    }

    /**
     * Handles the HTTP request to search within CSV data.
     *
     * @param request - the HTTP request.
     * @param response - the HTTP response.
     * @return - serialized response containing search results.
     * @throws Exception - if there is an error handling the request.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // create a map to hold CSV search results
        Map<String, Object> responseMap = new HashMap<>();
        // check if CSVDataSource is initialized
        if (source.getCurrentMatrix() == null) {
            // if not initialized, return a failure response map
            responseMap.put("result", "error");
            responseMap.put("message", "No data source initialized");
            return new CSVFailureResponse("error", responseMap).serialize();
        }
        // retrieve the current CSV matrix
        List<List<String>> mtrx = source.getCurrentMatrix().mtrx();
        // parse request parameters
        Options[] options = new Options[]{UtilitySearch.Options.NONE, UtilitySearch.Options.NONE, UtilitySearch.Options.NONE};
        String val = request.queryParams("val");
        String colId = request.queryParams("colId");
        String opts = request.queryParams("opts");
        // validate parameters
        if (val == null) {
            throw new ColumnConversionException(Errors.ARGERR_MAIN.report());
        }
        // set search options
        if (opts != null) {
            if (opts.contains("h")) {
                options[0] = UtilitySearch.Options.HEADER;
            }
            if (opts.contains("s")) {
                options[1] = UtilitySearch.Options.CASE_SEN;
            }
            if (opts.contains("m")) {
                options[2] = UtilitySearch.Options.MTCH_LOCK;
            }
        }
        // perform search operations
        UtilitySearch search = new UtilitySearch(mtrx, options);
        List<Integer> toPrint = new ArrayList<>();
        int row;
        if (colId == null) {
            while ((row = search.search(val)) != -1) toPrint.add(row);
        } else {
            while ((row = search.search(val, colId)) != -1) toPrint.add(row);
        }
        // construct response map
        Map<String, Object> resMap = new HashMap<>();
        // prepare and return response
        if (!toPrint.isEmpty()) {
            resMap.put("csvData", toPrint.stream().map(mtrx::get).collect(Collectors.toList()));
            return new CSVSuccessResponse(resMap).serialize();
        } else {
            String errorRes;
            if (colId == null) {
                errorRes = "Value '" + val + "' not found within column '" + colId + "'";
                System.err.println("Value '" + val + "' not found within column '" + colId + "'");
            } else {
                errorRes = "Value '" + val + "' not found";
                System.err.println("Value '" + val + "' not found");
            }
            resMap.put("csvData", null);
            return new CSVFailureResponse(errorRes, null).
                    serialize();
        }
    }

    /**
     * Represents a successful response to a CSV search request.
     *
     * @param response_type - the type of the response.
     * @param responseMap - the response map containing the CSV data.
     */
    public record CSVSuccessResponse(String response_type, Map<String, Object> responseMap) {

        /**
         * Constructs a CSVSuccessResponse with the given response.
         *
         * @param responseMap - the response map containing CSV search results.
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
     * @param response_type - the type of the response.
     * @param responseMap - the response map containing CSV search results.
     */
    public record CSVFailureResponse(String response_type, Map<String, Object> responseMap) {

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