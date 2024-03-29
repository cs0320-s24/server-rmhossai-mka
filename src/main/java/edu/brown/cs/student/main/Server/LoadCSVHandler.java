package edu.brown.cs.student.main.Server;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.ParserUtils.CSVParser;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import edu.brown.cs.student.main.Server.ViewCSVHandler.CSVFailureResponse;
import edu.brown.cs.student.main.Server.ViewCSVHandler.CSVSuccessResponse;
import java.util.Map;
import java.util.HashMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Quick Summary:
 * Awaits requests to load specific CSV files.
 * Retrieves the file path from request query parameters.
 * Attempts to open the specified file.
 * Initializes a CSV parser capable of deserializing CSV data.
 * Processes the file's content, transforming it into a data matrix.
 * Retains the parsed data for further use.
 * Provides the parsed data as the HTTP response.
 */

/**
 * This class implements the Route interface to handle HTTP requests for loading CSV files.
 */
public class LoadCSVHandler implements Route {
    private CSVDataSource source;

    /**
     * Constructor injects a CSVDataSource object for data management.
     *
     * @param source - the CSVDataSource object to use.
     */
    public LoadCSVHandler(CSVDataSource source){
        this.source = source;
    }

    /**
     * Handles a request to load a CSV file and returns the parsed data.
     *
     * @param request - the HTTP request object.
     * @param response - the HTTP response object.
     * @return - the parsed data matrix as a List<List<String>>.
     * @throws Exception - if an error occurs during file loading or parsing.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // extract file path from request query parameters
        String filepath = System.getProperty("user.dir") + "/data/" + request.queryParams("filepath");
        // check if request or filepath is null, if so, return a failure response
        if (filepath == null) {
            return new CSVFailureResponse("error", "Filepath unspecified").serialize();
        }
        // attempt to open the CSV file
        FileReader reader = null;
        try {
            reader = new FileReader(filepath);
        } catch (FileNotFoundException e) {
            return new CSVFailureResponse("error", "Filepath " + filepath +
                    " not found").serialize();
        }
        // create a CSVParser object with BufferedReader and StrListCreatorFromRow for deserialization
        CSVParser<List<String>> parser = new CSVParser<>(new BufferedReader(reader), new StrListCreatorFromRow());
        // parse the CSV file and capture the parsed data matrix.
        List<List<String>> mtrx = null;
        try {
            mtrx = parser.parse();
        } catch (FactoryFailureException e) {
            return new CSVFailureResponse("error", "Parse error").serialize();
        }

        // store the parsed data in the CSVDataSource object
        source.setCurrentMatrix(mtrx);
        // return the parsed data string response

        return new CSVSuccessResponse(filepath).serialize();
    }

    /**
     * Represents a successful response to a CSV request.
     *
     * @param result - the type of the response.
     * @param filepath - the filepath containing CSV data.
     */
    public record CSVSuccessResponse(String result, String filepath) {

        /**
         * Constructs a CSVSuccessResponse with the given response map.
         *
         * @param filepath - the filepath containing CSV data.
         */
        public CSVSuccessResponse(String filepath) {
            this("success", filepath);
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
                // print stack trace if the serialization fails
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Represents a failure response to a CSV request.
     *
     * @param result - the type of the response.
     * @param msg - an error message to display.
     */
    public record CSVFailureResponse(String result, String msg) {

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