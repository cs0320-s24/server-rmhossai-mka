package edu.brown.cs.student.main.DataSource;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class ACSDataSourceProxy {

    public final LoadingCache<String, Double> cache;

    private final ACSDataSource

    public ACSDataSourceProxy(int maxSize, long expireAfterWriteDuration,
                          TimeUnit timeUnit) {

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
}
