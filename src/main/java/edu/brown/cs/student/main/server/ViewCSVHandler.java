package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Parser.CSVParser;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class ViewCSVHandler implements Route  {

    CSVParser<List<String>> parser;

    public ViewCSVHandler(CSVParser parser){
        this.parser = parser;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        List<List<String>> mtrx = parser.parse();

        return new CSVSuccessResponse(mtrx).serialize();
    }

    public record CSVSuccessResponse(String response_type,
                                     List<List<String>> responseList) {

        public CSVSuccessResponse(List<List<String>> responseList) {
            this("success", responseList);
        }

        String serialize() {
            try {
                // Initialize Moshi which takes in this class and returns it as JSON!
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<CSVSuccessResponse> adapter =
                        moshi.adapter(CSVSuccessResponse.class);
                return adapter.toJson(this);
            } catch (Exception e) {
                // For debugging purposes, show in the console _why_ this fails
                // Otherwise we'll just get an error 500 from the API in integration
                // testing.
                e.printStackTrace();
                throw e;
            }
        }
    }

    public record CSVFailureResponse(String response_type,
                                     List<List<String>> responseList) {

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(CSVFailureResponse.class).toJson(this);
        }
    }
}
