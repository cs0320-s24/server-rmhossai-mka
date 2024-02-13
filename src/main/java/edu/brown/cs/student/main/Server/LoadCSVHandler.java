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
 * Implements the Route interface, which requires defining a handle method to process incoming HTTP requests.
 * CSVParser object is initialized with a StrListCreatorFromRow object for deserializing CSV data into a list of strings.
 * handle method retrieves the file path of the CSV file to be loaded from the request query parameters.
 * FileReader is created for the CSV file using the file path.
 * New CSVParser object is created with a BufferedReader created from the FileReader.
 * New CSVParser object is returned as the response to the HTTP request.
 */

public class LoadCSVHandler implements Route {
    private CSVDataSource source;

    /**
     * Creates a new LoadCSVHandler instance with the given CSVParser.
     *
     * @param source -
     */
    public LoadCSVHandler(CSVDataSource source){
        this.source = source;
    }

    /**
     * Handles a request to load a CSV file and returns a CSVParser object.
     *
     * @param request - the HTTP request object.
     * @param response - the HTTP response object.
     * @return - a CSVParser object.
     * @throws Exception - if there is any error loading the CSV file.
     */
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // gets the file path from the query parameters
        // initializes a FileReader object for the CSV file

        // returns the CSVParser object

        String filepath = System.getProperty("user.dir") +
                "/data/" + request.queryParams("filepath");

        FileReader reader = null;
        try {
            reader = new FileReader(filepath);
        } catch (FileNotFoundException e) {
            throw new DatasourceException(e.getMessage());
        }

        CSVParser<List<String>> parser =
                new CSVParser<>(new BufferedReader(reader), new StrListCreatorFromRow());

        List<List<String>> mtrx = null;
        try {
            mtrx = parser.parse();
        } catch (FactoryFailureException e) {
            throw new DatasourceException(e.getMessage());
        }

        source.setCurrentMatrix(mtrx);

        return source.getCurrentMatrix();
    }
}