package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.DataSource.CSVData;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.ParserUtils.CSVParser;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;

/**
 * Quick Summary:
 * The LoadCSVHandler class's main functionality is to handle HTTP requests for loading CSV files, parsing them, and
 * storing the parsed data in a CSVDataSource object. To test this class, we need to verify its behavior under various
 * scenarios, including successful parsing, handling file not found errors, and handling parsing failures.
 */
public class LoadCSVHandlerTest {
  LoadCSVHandler loadCSVHandler;
  CSVDataSource csvDataSource;

  // define CSVDataSourceStub within the test class
  static class CSVDataSourceStub implements CSVDataSource {
    List<List<String>> currentMatrix;

    @Override
    public CSVData getCurrentMatrix() {
      return new CSVData(currentMatrix);
    }

    @Override
    public void setCurrentMatrix(List<List<String>> matrix) throws DatasourceException {
      this.currentMatrix = matrix;
    }
  }

  @BeforeEach
  void setUp() {
    csvDataSource = new CSVDataSourceStub();
    loadCSVHandler = new LoadCSVHandler(csvDataSource);
  }

  @Test
  void testHandle_SuccessfulParse() throws Exception {
    String filepath = "ri_income_estimates.csv";
    String csvData = ""; // Read CSV data from file

    // Parse CSV data
    BufferedReader reader = new BufferedReader(new FileReader(filepath));
    CSVParser<List<String>> parser = new CSVParser<>(reader, new StrListCreatorFromRow());
    List<List<String>> expectedMatrix = parser.parse();

    // Handle the request

    // Assert that the current matrix in CSVDataSourceStub is as expected
    assertEquals(expectedMatrix, ((CSVDataSourceStub) csvDataSource).currentMatrix);
  }

  @Test
  void testHandle_FileNotFound() {
    String filepath = "non_existing_file.csv";
    //assertThrows(DatasourceException.class, () -> loadCSVHandler.handle
    // (filepath));
  }
}
