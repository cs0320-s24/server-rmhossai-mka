package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Constants.Errors;
import edu.brown.cs.student.main.DataSource.CSVData;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.DataSource.DatasourceException;
import edu.brown.cs.student.main.Exceptions.ColumnConversionException;
import edu.brown.cs.student.main.Parser.CSVParser;
import edu.brown.cs.student.main.Search.UtilitySearch;
import edu.brown.cs.student.main.Search.UtilitySearch.Options;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchCSVHandler implements Route {

    private CSVDataSource source;

    public SearchCSVHandler(CSVDataSource source){
        this.source = source;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        if (source.getCurrentMatrix() == null) {
            throw new DatasourceException("No data source initialized");
        }

        List<List<String>> mtrx = source.getCurrentMatrix().mtrx();


        Options[] options = new Options[]{UtilitySearch.Options.NONE,
                UtilitySearch.Options.NONE, UtilitySearch.Options.NONE};

        UtilitySearch search = new UtilitySearch(mtrx, options);
        String val = request.queryParams("val");
        String colId = request.queryParams("colId");
        String opts = request.queryParams("opts");

        if (val == null) {
            throw new ColumnConversionException(Errors.ARGERR_MAIN.report());
        }

        if (opts != null) {
            if (opts.contains("H")) {
                options[0] = UtilitySearch.Options.HEADER;
            }
            if (opts.contains("S")) {
                options[1] = UtilitySearch.Options.CASE_SEN;
            }
            if (opts.contains("M")) {
                options[2] = UtilitySearch.Options.MTCH_LOCK;
            }
        }

        List<Integer> toPrint = new ArrayList<>();

        int row;
        if (colId == null) {
            while ((row = search.search(val)) != -1) toPrint.add(row);
        } else {
            while ((row = search.search(val, colId)) != -1) toPrint.add(row);
        }

        if (!toPrint.isEmpty()) {
            return toPrint.stream().map(mtrx::get).collect(Collectors.toList());
        } else {
            if (colId == null) {
                System.err.println(
                        "Value '" + val + "' not " + "found" + "within " +
                                "column '" + colId + "'");
            } else {
                System.err.println("Value '" + val + "' not found");
            }
            return null;
        }
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
