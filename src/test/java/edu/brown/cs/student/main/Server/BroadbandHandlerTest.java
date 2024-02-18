package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.DataSource.ACSDataSourceProxy;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class BroadbandHandlerTest {

  private ACSDataSourceProxy acsDataSource;
  private JsonAdapter<Map<String, Object>> adapter;
  private final String apiService = "broadband";
  private final String stateParam = "state";
  private final String countyParam = "county";

  @BeforeAll
  public static void setupOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() {
    acsDataSource = new ACSDataSourceProxy();
    Spark.get("/" + apiService, new BroadbandHandler(acsDataSource));
    Spark.awaitInitialization();
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/" + apiService);
    Spark.awaitStop();
  }

  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }

  // Test for successful broadband percentage retrieval
  @Test
  void testHandle_SuccessfulDataRetrieval() throws Exception {
    // Make a request to the handler
    HttpURLConnection connection = tryRequest(apiService + "?state=Rhode+Island&county=Providence");
    assertEquals(200, connection.getResponseCode());

    // Deserialize the response
    Map<String, Object> responseBody = adapter.fromJson(connection.getInputStream().source());
    showDetailsIfError(responseBody);

    // Assert response
    assertEquals("success", responseBody.get("result"));
    assertEquals("Rhode Island", responseBody.get("state"));
    assertEquals("Providence", responseBody.get("county"));
    assertEquals(80.0, responseBody.get("broadBandPercentage")); // Mocked broadband percentage

    // Disconnect the connection
    connection.disconnect();
  }

  // Test case when state parameter is missing
  @Test
  void testMissingStateParameter() throws IOException {
    try {
      String county = "Providence County";

      HttpURLConnection connection = tryRequest(apiService + "?" + countyParam + "=" + county);
      assertEquals(200, connection.getResponseCode());

      Map<String, Object> responseBody = adapter.fromJson(connection.getInputStream().source());
      showDetailsIfError(responseBody);

      assertEquals("error", responseBody.get("result"));
      assertEquals("Missing state parameter", responseBody.get("msg"));

      connection.disconnect();
    } catch (DatasourceException e) {
      e.printStackTrace();
      fail("Failed to fetch broadband data: " + e.getMessage());
    }
  }

  // Test case when the county parameter is missing
  @Test
  void testHandle_MissingCountyParameter() throws Exception {
    // Make a request without the county parameter
    HttpURLConnection connection = tryRequest(apiService + "?state=Rhode+Island");
    assertEquals(400, connection.getResponseCode());

    // Deserialize the response
    Map<String, Object> responseBody = adapter.fromJson(connection.getErrorStream().source());
    showDetailsIfError(responseBody);

    // Assert response
    assertEquals("error", responseBody.get("response_type"));
    assertTrue(responseBody.get("msg").toString().contains("Missing required parameter 'county'"));

    // Disconnect the connection
    connection.disconnect();
  }
}
