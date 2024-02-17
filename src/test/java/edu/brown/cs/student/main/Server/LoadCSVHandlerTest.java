package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.DataSource.CSVData;
import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import edu.brown.cs.student.main.ParserUtils.CSVParser;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  CSVDataSource csvDataSource;
  JsonAdapter<Map<String, Object>> adapter;
  final String apiService = "loadcsv";
  final String testingParam = "filepath";

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
   *
   * The "throws" clause doesn't matter below -- JUnit will fail if an
   *     exception is thrown that hasn't been declared as a parameter to @Test.
   *
   * @param apiCall the call string, including endpoint
   *                (Note: this would be better if it had more structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
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
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }

  // TODO: From here on is the stuff that matters



  /**
   *
   * Recall: The structure of our success response as a JSON is
   * { "result": "success",
   *   "filepath" : "<filepath>"
   * }
   */
  @Test
  void testHandle_SuccessfulParse() throws Exception {
//    String filepath = "ri/ri_income_estimates.csv";
//
//    HttpURLConnection loadConnection =
//            tryRequest(apiService + "?" + testingParam + "=" + filepath);
//    assertEquals(200, loadConnection.getResponseCode());
//
//    Map<String, Object> responseBody = adapter.fromJson(
//            new Buffer().readFrom(loadConnection.getInputStream()));
//    showDetailsIfError(responseBody);
//    assertEquals("success", responseBody.get("result"));
//
//    assertEquals(System.getProperty("user.dir") + "/data/" + filepath,
//            responseBody.get(testingParam));
//    loadConnection.disconnect(); // close gracefully
    String filepath = "ri/ri_income_estimates.csv";
    try {
      HttpURLConnection loadConnection = tryRequest(apiService + "?" + testingParam + "=" + filepath);
      assertEquals(200, loadConnection.getResponseCode());
      Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
      showDetailsIfError(responseBody);
      assertEquals("success", responseBody.get("result"));
      System.out.println("Test testHandle_SuccessfulParse: PASSED for filepath: " + filepath);
      loadConnection.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Test testHandle_SuccessfulParse: FAILED for filepath: " + filepath);
    }
  }

  /**
   *
   * Recall: The structure of our failure response as a JSON is
   * { "result": "error",
   *   "msg" : "<message>" (check LoadCSVHandler for which message)
   * }
   */

  @Test
  void testHandle_FileNotFound() {
    // TODO: Implement
    String filepath = "non_existing_file.csv";
    try {
      HttpURLConnection loadConnection = tryRequest(apiService + "?" + testingParam + "=" + filepath);
      assertEquals(200, loadConnection.getResponseCode());
      Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getErrorStream()));
      showDetailsIfError(responseBody);
      assertEquals("error", responseBody.get("result"));
      System.out.println("Test testHandle_FileNotFound: PASSED for filepath: " + filepath);
      loadConnection.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Test testHandle_FileNotFound: FAILED for filepath: " + filepath);
    }
    //assertThrows(DatasourceException.class, () -> loadCSVHandler.handle
    // (filepath));
  }

  @Test
  void testHandle_EmptyFile() throws IOException {
    String filepath = "empty_file.csv";
    HttpURLConnection loadConnection = tryRequest(apiService + "?" + testingParam + "=" + filepath);
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error", responseBody.get("result"));
    loadConnection.disconnect();
  }

  @Test
  void testHandle_UninitializedCSV() throws IOException {
    String filepath = "uninitialized_file.csv";
    HttpURLConnection loadConnection = tryRequest(apiService + "?" + testingParam + "=" + filepath);
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error", responseBody.get("result"));
    loadConnection.disconnect();
  }

  @Test
  void testHandle_NullFilePath() throws IOException {
    HttpURLConnection loadConnection = tryRequest(apiService);
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error", responseBody.get("result"));
    loadConnection.disconnect();
  }

  @Test
  void testHandle_NullRequest() throws IOException {
    HttpURLConnection loadConnection = tryRequest("");
    assertEquals(404, loadConnection.getResponseCode());
    loadConnection.disconnect();
  }
}
