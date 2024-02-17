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
 * CacheBuilder.newBuilder() is used to create a new CacheBuilder instance.
 * maximumSize(maxSize) sets the maximum size of the cache.
 * expireAfterWrite(expireAfterWriteDuration, timeUnit) sets the expiration duration for cache entries.
 * build(new CacheLoader<String, Double>() { ... }) constructs the LoadingCache instance with a CacheLoader
 * that defines how to load cache entries when they are not present.
 */
public class ACSDatasource {
    // define cache properties
    public final LoadingCache<String, Double> cache;

    public static Map<String, Integer> stateCodes;

    public static Map<Integer, Map<String, Integer>> countyCodes;

    public ACSDatasource(int maxSize, long expireAfterWriteDuration, TimeUnit timeUnit) {

      /*
      * State Code (all): https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
        County Codes (all) https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:* (can also add &in=state:* for a specific state)
        Broadband Data (S2802_C03_022E = Broadband Data Estimates) : https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:* (can also add &in=state:* for a specific state)

        key (although I don't actually think it's required lol):
        ea1e4110a03fef925f4c2b1670c951365d5b8e02
      * */
        // configure cache
        this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(expireAfterWriteDuration, timeUnit).build(
        new CacheLoader<String, Double>() {
          @Override
          public Double load(String s) throws Exception {
            // blah blah implement fetching of broadband percentage from ACS API and return that
            return null;
          }
        });
    }

    /*
        URL requestURL = new URL("https", "api.weather.gov", "/points/"+lat+","+lon);
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();
        // Create a URL to connect and set up Moshi

        JsonAdapter<GridResponse> adapter = moshi.adapter(GridResponse.class).nonNull();
        // Set up an adapter; this will allow you to de/serialize data.

        // From here you’d fetch the state codes and county codes; I
     */

    private static double fetchStateCodes() throws DatasourceException{
        // URL to model after: https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*
        try {
            // TODO 1: Fill out this stubbed URL based on the above
            // construct the URL for state codes API request
            URL requestURL = new URL("https://", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");

            // establish the URL connection
            HttpURLConnection clientConnection = connect(requestURL);
            Moshi moshi = new Moshi.Builder().build();

            // TODO 2: Change this adapter so that it returns the correct
            //  return type. You should be looking through the static
            //  "stateCodes" and initializing it via the adapter below.
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
          if (response == null || response.isEmpty()) {
            throw new DatasourceException("Malformed response from Census API: No data returned");
          }

          // iterate over response to populate the stateCodes map
          for (List<Object> data : response) {
            String stateName = (String) data.get(0);
            int stateCode = Integer.parseInt((String) data.get(1)); // assuming the state code is in the second position (index 1)
            stateCodes.put(stateName, stateCode);
          }

          // return any arbitrary double value since the method signature requires it
          return 0.0;
        } catch (IOException e) {
          throw new DatasourceException("Error fetching state codes: " + e.getMessage());
        }
    }

    private static double fetchCountyCodes() throws DatasourceException{
      // URL to model after: https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*
      try {
        // TODO 1: Fill out this stubbed URL based on the above
        // construct the URL for county codes API request
        URL requestURL = new URL("https://", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*");

        // establish the URL connection
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();

        // TODO 2: Change this adapter so that it returns the correct
        //  return type. You should be looking through the static
        //  "stateCodes" and initializing it via the adapter below.
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
        if (response == null || response.isEmpty()) {
          throw new DatasourceException("Malformed response from Census API: No data returned");
        }

        // iterate over response to populate the countyCodes map
        for (List<Object> data : response) {
          // assuming the county code is in the second position (index 1) and state code is in the third position (index 2)
          int countyCode = Integer.parseInt((String) data.get(1));
          int stateCode = Integer.parseInt((String) data.get(2));
          // initialize inner map if not present
          countyCodes.putIfAbsent(stateCode, new HashMap<>());
          // put the county code in the inner map
          countyCodes.get(stateCode).put((String) data.get(0), countyCode);
        }
        // return any arbitrary double value since the method signature requires it
        return 0.0;
      } catch (IOException e) {
        throw new DatasourceException("Error fetching county codes: " + e.getMessage());
      }
    }

    public static double fetchBroadbandPercentage(String state, String county) throws DatasourceException {
      try {
          Integer stateCode = stateCodes.get(state);
          Integer countyCode = countyCodes.get(stateCode).get(county);


          URL requestURL = new URL("https", "api.census.gov",
                  "/data/2021/acs/acs1/subject/variables?get=NAME," +
                          "S2802_C03_022E&for=county:" + state + "&in=state:" + county);
          HttpURLConnection clientConnection = connect(requestURL);
          Moshi moshi = new Moshi.Builder().build();

          JsonAdapter<Double> adapter =
                  moshi.adapter(Double.class).nonNull();

          Double body =
                  adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
          clientConnection.disconnect();
          if(body == null)
              throw new DatasourceException("Malformed response from NWS");
          return body;
      } catch (IOException e){
          throw new DatasourceException(e.getMessage());
        }
    }

    public static Map<String, Integer> getStatesCodes(){
        return stateCodes;
    }

    public static Map<Integer, Map<String, Integer>> getCountyCodes(){
        return countyCodes;
    }

    /**
     * Private helper method; throws IOException so different callers
     * can handle differently if needed.
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

  // method to get broadband percentage from cache or ACS API
    public double getBroadbandPercentage(String state, String county) throws Exception {
        String key = state + ":" + county;
        return cache.get(key);
        // complete error handling implementation here
    }
}


/*
*     private static GridResponse resolveGridCoordinates(double lat, double lon) throws DatasourceException {
        try {
            URL requestURL = new URL("https", "api.weather.gov", "/points/"+lat+","+lon);
            HttpURLConnection clientConnection = connect(requestURL);
            Moshi moshi = new Moshi.Builder().build();

            // NOTE WELL: THE TYPES GIVEN HERE WOULD VARY ANYTIME THE RESPONSE TYPE VARIES
            JsonAdapter<GridResponse> adapter = moshi.adapter(GridResponse.class).nonNull();
            // NOTE: important! pattern for handling the input stream
            GridResponse body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();
            if(body == null || body.properties() == null || body.properties().gridId() == null)
                throw new DatasourceException("Malformed response from NWS");
            return body;
        } catch(IOException e) {
            throw new DatasourceException(e.getMessage());
        }
    }
* */