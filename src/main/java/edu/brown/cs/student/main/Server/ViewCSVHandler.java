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

public class ViewCSVHandler implements Route  {

    private CSVDataSource source;

    public ViewCSVHandler(CSVDataSource source){
        this.source = source;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (source.getCurrentMatrix() == null) {
            throw new DatasourceException("No data source initialized");
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("csvData", source.getCurrentMatrix());
        System.out.println(source.getCurrentMatrix());

        return new CSVSuccessResponse(resMap).serialize();
    }

    public record CSVSuccessResponse(String response_type,
                                     Map<String, Object> responseMap) {

        public CSVSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
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
                                     Map<String, Object> responseMap) {

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(CSVFailureResponse.class).toJson(this);
        }
    }
}
