package edu.brown.cs.student.main.Server;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

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

  public ACSDatasource(int maxSize, long expireAfterWriteDuration, TimeUnit timeUnit) {
    // configure cache
    this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterWrite(expireAfterWriteDuration, timeUnit).build(
        new CacheLoader<String, Double>() {
          @Override
          public Double load(String s) throws Exception {
            // blah blah implement fetching of broadband percentage from ACS API and return that
            return fetchBroadbandPercentageFromACS(s);
          }
        });
  }

  // method to get broadband percentage from cache or ACS API
  public double getBroadbandPercentage(String state, String county) throws Exception {
    String key = state + ":" + county;
    return cache.get(key);
    // complete error handling implementation here
  }

  // Method to fetch broadband percentage from ACS API
  public double fetchBroadbandPercentageFromACS(String key) {
    // Implement logic to fetch broadband percentage from ACS API
    // For demonstration purposes, returning a dummy value
    return Math.random() * 100; // Dummy value between 0 and 100
  }
}
