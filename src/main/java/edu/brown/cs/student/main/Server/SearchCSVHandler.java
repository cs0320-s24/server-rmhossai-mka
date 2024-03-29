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
     * @param result - the HTTP request.
     * @param data - the HTTP response.
     * @return - serialized response containing search results.
     * @throws Exception - if there is an error handling the request.
     */
    @Override
    public Object handle(Request result, Response data) throws Exception {
        // create a map to hold CSV search results
        // check if CSVDataSource is initialized
        if (source.getCurrentMatrix() == null) {
            // if not initialized, return a failure response map
            return new CSVFailureResponse("error", "No data source initialized").serialize();
        }
        // retrieve the current CSV matrix
        List<List<String>> mtrx = source.getCurrentMatrix().mtrx();
        // parse request parameters
        Options[] options = new Options[]{UtilitySearch.Options.NONE, UtilitySearch.Options.NONE, UtilitySearch.Options.NONE};
        String val = result.queryParams("val");
        String colId = result.queryParams("colId");
        String opts = result.queryParams("opts");
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

        if (!toPrint.isEmpty()) {
            return new CSVSuccessResponse(toPrint.stream().map(mtrx::get).collect(Collectors.toList())).serialize();
        } else {
            String errorRes;
            if (colId == null) {
                errorRes = "Value '" + val + "' not found within column '" + colId + "'";
                System.err.println("Value '" + val + "' not found within column '" + colId + "'");
            } else {
                errorRes = "Value '" + val + "' not found";
                System.err.println("Value '" + val + "' not found");
            }
            return new CSVFailureResponse("error", errorRes).
                    serialize();
        }
    }

    /**
     * Represents a successful response to a CSV search request.
     *
     * @param result - the type of the response.
     * @param data - the response list containing the CSV data.
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