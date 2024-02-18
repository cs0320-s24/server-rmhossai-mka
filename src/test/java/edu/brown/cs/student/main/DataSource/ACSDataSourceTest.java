package edu.brown.cs.student.main.DataSource;

import edu.brown.cs.student.main.Exceptions.DatasourceException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ACSDataSourceTest {

  // Test fetching state codes
  @Test
  void testFetchStateCodes() {
    try {
      ACSDataSource.fetchStateCodes();
      Map<String, Integer> stateCodes = ACSDataSource.getStatesCodes();
      assertNotNull(stateCodes);
      assertTrue(stateCodes.size() > 0);
    } catch (DatasourceException e) {
      fail("Exception occurred: " + e.getMessage());
    }
  }

  // Test fetching county codes for a specific state
  @Test
  void testFetchCountyCodes() {
    try {
      ACSDataSource.fetchStateCodes(); // Ensure state codes are fetched first
      ACSDataSource.fetchCountyCodes("Rhode Island");
      Map<Integer, Map<String, String>> countyCodes = ACSDataSource.getCountyCodes();
      assertNotNull(countyCodes);
      assertTrue(countyCodes.size() > 0);
    } catch (DatasourceException e) {
      fail("Exception occurred: " + e.getMessage());
    }
  }

  // Test fetching broadband percentage estimate
  @Test
  void testFetchBroadbandPercentage() {
    try {
      double broadbandPercentage = ACSDataSource.fetchBroadbandPercentage("Rhode Island", "Providence");
      assertTrue(broadbandPercentage >= 0 && broadbandPercentage <= 100);
    } catch (DatasourceException e) {
      fail("Exception occurred: " + e.getMessage());
    }
  }
}
