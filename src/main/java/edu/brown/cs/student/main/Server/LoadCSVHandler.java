package edu.brown.cs.student.main.Server;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.DataSource.DatasourceException;
import edu.brown.cs.student.main.DataSource.GeneralCSVDataSource;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Parser.CSVParser;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

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
        // attempt to open the CSV file
        FileReader reader = null;
        try {
            reader = new FileReader(filepath);
        } catch (FileNotFoundException e) {
            throw new DatasourceException(e.getMessage());
        }
        // create a CSVParser object with BufferedReader and StrListCreatorFromRow for deserialization
        CSVParser<List<String>> parser = new CSVParser<>(new BufferedReader(reader), new StrListCreatorFromRow());
        // parse the CSV file and capture the parsed data matrix.
        List<List<String>> mtrx = null;
        try {
            mtrx = parser.parse();
        } catch (FactoryFailureException e) {
            throw new DatasourceException(e.getMessage());
        }
        // store the parsed data in the CSVDataSource object
        source.setCurrentMatrix(mtrx);
        // return the parsed data string response
        return "Parse was successful!";
    }
}