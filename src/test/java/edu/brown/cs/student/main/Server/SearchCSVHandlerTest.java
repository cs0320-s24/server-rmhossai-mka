package edu.brown.cs.student.main.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.DataSource.CSVData;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Quick Summary:
 * The LoadCSVHandler class's main functionality is to handle HTTP requests for loading CSV files, parsing them, and
 * storing the parsed data in a CSVDataSource object. To test this class, we need to verify its behavior under various
 * scenarios, including successful parsing, handling file not found errors, and handling parsing failures.
 */
public class SearchCSVHandlerTest {

  CSVDataSource csvDataSource;
  JsonAdapter<Map<String, Object>> adapter;
  final String apiService = "searchcsv";
  final String testingParam = "data";

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

  @BeforeAll
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

    // Use *MOCKED* data when in this test environment.
    // Notice that the WeatherHandler code doesn't need to care whether it has
    // "real" data or "fake" data. Good separation of concerns enables better testing.
    csvDataSource = new CSVDataSourceStub();
    Spark.get("/" + apiService, new LoadCSVHandler(csvDataSource));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    //   For more on this, see the Server gearup.
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class,
        Object.class));
  }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/" + apiService);
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   * <p>
   * The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @param apiCall the call string, including endpoint (Note: this would be better if it had more
   *                structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper to make working with a large test suite easier: if an error, print more info.
   *
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }

  // TODO 1: Start here

  @Test
  void testSuccessfulSearch() throws IOException, DatasourceException {
    // Prepare test data
    List<List<String>> testData = List.of(
        List.of("Header1", "Header2"),
        List.of("Value1", "Value2")
    );
    try {
      csvDataSource.setCurrentMatrix(testData);

      // Make the request to the handler
      HttpURLConnection connection = tryRequest(apiService + "?val=Value1&colId=Header1");
      assertEquals(200, connection.getResponseCode());

      // Deserialize the response
      Map<String, Object> responseBody = adapter.fromJson(connection.getInputStream().source());
      showDetailsIfError(responseBody);

      // Assert response
      assertEquals("success", responseBody.get("result"));
      assertTrue(responseBody.get("data") instanceof List);

      // Disconnect the connection
      connection.disconnect();
    } catch (DatasourceException e) {
      // Handle the exception if needed
      e.printStackTrace(); // For example, print the stack trace
      fail("Failed to set current matrix: " + e.getMessage());
    }
  }

  @Test
  void testUninitializedDataSource() throws IOException {
    try {
      // Set the data source to null (uninitialized)
      csvDataSource.setCurrentMatrix(null);

      // Make the request to the handler
      HttpURLConnection connection = tryRequest(apiService + "?val=Value1&colId=Header1");
      assertEquals(200, connection.getResponseCode());

      // Deserialize the response
      Map<String, Object> responseBody = adapter.fromJson(connection.getInputStream().source());
      showDetailsIfError(responseBody);

      // Assert response
      assertEquals("error", responseBody.get("result"));
      assertEquals("No data source initialized", responseBody.get("msg"));

      // Disconnect the connection
      connection.disconnect();
    } catch (DatasourceException e) {
      // Handle the exception if needed
      e.printStackTrace(); // For example, print the stack trace
      fail("Failed to set current matrix: " + e.getMessage());
    }
  }

  @Test
  void testInvalidParameters() throws IOException {
    // Make the request to the handler with invalid parameters (missing required parameters)
    HttpURLConnection connection = tryRequest(apiService);
    assertEquals(500, connection.getResponseCode()); // Assuming 500 for invalid request

    // Deserialize the response
    Map<String, Object> responseBody = adapter.fromJson(connection.getInputStream().source());
    showDetailsIfError(responseBody);

    // Assert response
    assertEquals("error", responseBody.get("type"));
    assertEquals("Bad Request", responseBody.get("msg"));

    // Disconnect the connection
    connection.disconnect();
  }

}
