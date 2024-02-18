package edu.brown.cs.student.main.DataSource;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonAdapter.Factory;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okio.Buffer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.jetbrains.annotations.Nullable;

/**
 * Quick Summary:
 * The ACSDatasource class provides functionality to interact with the Census Bureau's API
 * and cache data related to state codes, county codes, and broadband percentage estimates.
 * CacheBuilder.newBuilder() is used to create a new CacheBuilder instance, allowing for efficient
 * caching of data retrieved from the API. The maximumSize(maxSize) method sets the maximum size of the cache,
 * while expireAfterWrite(expireAfterWriteDuration, timeUnit) specifies the expiration duration for cache entries,
 * ensuring that cached data remains fresh. The build(new CacheLoader<String, Double>() { ... }) method constructs
 * the LoadingCache instance with a CacheLoader that defines how to load cache entries when they are not present,
 * enhancing performance by avoiding redundant API calls.
 */

/**
 * Provides functionality to interact with the Census Bureau's API and cache data related to state codes,
 * county codes, and broadband percentage estimates.
 */
public class ACSDataSource {
    // define cache properties
    public final LoadingCache<String, Double> cache;
    // define maps to store state codes and county codes
    public static Map<String, Integer> stateCodes;
    public static Map<Integer, Map<String, String>> countyCodes;

    /**
     * Constructs an ACSDataSource object with the specified cache parameters.
     *
     * @param maxSize - the maximum size of the cache.
     * @param expireAfterWriteDuration - the expiration duration for cache entries in milliseconds.
     * @param timeUnit - the time unit for the expiration duration.
     */
    public ACSDataSource(int maxSize, long expireAfterWriteDuration, TimeUnit timeUnit) {
      /*
      * State Code (all): https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
        County Codes (all) https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:* (can also add &in=state:* for a specific state)
        Broadband Data (S2802_C03_022E = Broadband Data Estimates) : https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:* (can also add &in=state:* for a specific state)

        key (although I don't actually think it's required lol):
        ea1e4110a03fef925f4c2b1670c951365d5b8e02
      * */

        // configure the cache
        this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(expireAfterWriteDuration, timeUnit).build(
        new CacheLoader<String, Double>() {
          @Override
          public Double load(String s) throws Exception {
            // blah blah implement fetching of broadband percentage from ACS API and return that
            return null;
          }
        });
    }

    /**
     * Fetches state codes from the Census API and populates the stateCodes map.
     *
     * @return - an arbitrary double value (method signature requirement).
     * @throws DatasourceException - if an error occurs during the data fetching process.
     */
    private static double fetchStateCodes() throws DatasourceException{
        // URL to model after: https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
          try {
            // construct the URL for state codes API request
            URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");

            // establish the URL connection
            HttpURLConnection clientConnection = connect(requestURL);
            Moshi moshi = new Moshi.Builder().build();

            // create a JSON adapter for parsing the response
            Type responseType = Types.newParameterizedType(List.class, List.class, Object.class);
            JsonAdapter<List<List<Object>>> adapter = moshi.adapter(responseType);

            // read the response from the connection
            List<List<Object>> response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

            // end the connection
            clientConnection.disconnect();

            // initialize the stateCodes map
            stateCodes = new HashMap<>();

            // check if the response is null or empty
            if (response == null || response.isEmpty())
                throw new DatasourceException("Malformed response from Census API: No data returned");

            response.remove(0);
            // iterate over the response to populate the stateCodes map
            for (List<Object> data : response) {
                String stateName = String.valueOf(data.get(0));
                int stateCode = Integer.parseInt(String.valueOf(data.get(1))); //
                // assuming the state code is in the second position (index 1)
                stateCodes.put(stateName, stateCode);
            }

            // return any arbitrary double value since the method signature requires it
            return 0.0;
        } catch (IOException e) {
          throw new DatasourceException("Error fetching state codes: " + e.getMessage());
        }
    }

    /**
     * Fetches county codes from the Census API for a specific state and populates the countyCodes map.
     *
     * @param stateName - the code of the state for which county codes are
     *                fetched.
     * @return - an arbitrary double value (method signature requirement).
     * @throws DatasourceException - if an error occurs during the data fetching process.
     */
    private static double fetchCountyCodes(String stateName) throws DatasourceException{
      // URL to model after: https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*
      try {
          // construct the URL for county codes API request
          String urlString = "https://api.census" +
                  ".gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                  + stateCodes.get(stateName);

          URL requestURL = new URL(urlString);

          // establish the URL connection
          HttpURLConnection clientConnection = connect(requestURL);
          Moshi moshi = new Moshi.Builder().build();

          // create a JSON adapter for parsing the response
          Type responseType = Types.newParameterizedType(List.class, List.class, Object.class);
          JsonAdapter<List<List<Object>>> adapter = moshi.adapter(responseType);

          // read the response from the connection
          List<List<Object>> response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

          // end the connection
          clientConnection.disconnect();

          // initialize the countyCodes map
          countyCodes = new HashMap<>();

          // check if the response is null or empty
          if (response == null || response.isEmpty())
              throw new DatasourceException("Malformed response from Census API: No data returned");

          response.remove(0);
          // iterate over response to populate the countyCodes map
          for (List<Object> data : response) {
              // assuming the state code is in the second position (index 1)
              // and county code is in the third position (index 2)
              String countyCode = String.valueOf(data.get(2));
              int stateCode =
                      Integer.parseInt(String.valueOf(data.get(1)));
              // check if the county code belongs to the specified state

              // initialize inner map if not present
              countyCodes.putIfAbsent(stateCode, new HashMap<>());
              // put the county code in the inner map
              countyCodes.get(stateCode).put(String.valueOf(
                              data.get(0)),
                      countyCode);
          }
          // return any arbitrary double value since the method signature requires it
          return 0.0;
      } catch (IOException e) {
        throw new DatasourceException("Error fetching county codes: " + e.getMessage());
      }
    }

    /**
     * Fetches broadband percentage estimates for a specific state and county from the ACS API.
     *
     * @param state - the name of the state.
     * @param county - the name of the county.
     * @return - the broadband percentage estimate.
     * @throws DatasourceException - if an error occurs during the data fetching process.
     */
    public static double fetchBroadbandPercentage(String state, String county) throws DatasourceException {
      try {
          fetchStateCodes();
          fetchCountyCodes(state);
          // get the state code from the state name
          Integer stateCode = stateCodes.get(state);
          // get the county code from the county name and state code
          String countyCode = countyCodes.get(stateCode).get(county);

          // construct the URL for broadband data API request
          URL requestURL = new URL("https", "api.census.gov",
                  "/data/2021/acs/acs1/subject/variables?get=NAME," +
                          "S2802_C03_022E&for=county:" + countyCode + "&in" +
                          "=state:" + stateCode);

          // establish the URL connection
          HttpURLConnection clientConnection = connect(requestURL);
          Moshi moshi = new Moshi.Builder().build();

          // create the JSON adapter for parsing the response
          JsonAdapter<List<List<Object>>> adapter =
                  moshi.adapter(Types.newParameterizedType(List.class,
                          List.class,
                          Object.class));

          // read the response from the connection and parse the broadband percentage
          List<List<Object>> body =
                  adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

          // close the connection
          clientConnection.disconnect();

          // check if the response is null
          if(body == null)
              throw new DatasourceException("Malformed response from NWS");


          return Double.parseDouble(String.valueOf(body.get(1).get(1))); //
          // return the
          // broadband
          // percentage
      } catch (IOException e){
          throw new DatasourceException(e.getMessage());
        }
    }

    /**
     * Retrieves the map containing the state codes.
     *
     * @return - the map containing the state codes.
     */
    public static Map<String, Integer> getStatesCodes(){
        return stateCodes;
    }

    /**
     * Retrieves the map containing the county codes.
     *
     * @return - the map containing the county codes.
     */
    public static Map<Integer, Map<String, String>> getCountyCodes(){
        return countyCodes;
    }

    /**
     * Private helper method to establish an HTTP connection.
     *
     * @param requestURL - the URL to connect.
     * @return - the HttpURLConnection object.
     * @throws DatasourceException - if an error occurs during the connection process.
     * @throws IOException - if an I/O error occurs while opening the connection.
     */
    private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
        URLConnection urlConnection = requestURL.openConnection();
        if(! (urlConnection instanceof HttpURLConnection))
            throw new DatasourceException("unexpected: result of connection wasn't HTTP");
        HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
        clientConnection.connect(); // GET
        if(clientConnection.getResponseCode() != 200)
            throw new DatasourceException("unexpected: API connection not success status "+clientConnection.getResponseMessage());
        return clientConnection;
    }

    /**
     * Retrieves the broadband percentage from the cache or ACS API.
     *
     * @param state - the name of the state.
     * @param county - the name of the county.
     * @return - the broadband percentage.
     * @throws Exception - if an error occurs during the data fetching process.
     */
    public double getBroadbandPercentage(String state, String county) throws Exception {
        String key = state + ":" + county;
        return cache.get(key);
        // complete error handling implementation here
    }
}