package edu.brown.cs.student.main.Server;
import edu.brown.cs.student.main.DataSource.CSVData;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.DataSource.DatasourceException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Server.SearchCSVHandler.CSVFailureResponse;
import edu.brown.cs.student.main.Server.SearchCSVHandler.CSVSuccessResponse;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadbandHandler implements Route {
    public ACSDatasource acsDatasource;
    public BroadbandHandler(ACSDatasource acsDatasource){
        this.acsDatasource = acsDatasource;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // get state and county parameters
        String state = request.queryParams("state");
        String county = request.queryParams("county");
        // make request to ACS API to get broadband percentage
        double broadbandPercentage = acsDatasource.getBroadbandPercentage(state, county);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("state", state);
        responseMap.put("county", county);
        responseMap.put("broadbandPercentage", broadbandPercentage);
        // return results and parameters as JSON
        return new BroadbandSuccessResponse(responseMap).serialize();
    }

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