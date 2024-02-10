package edu.brown.cs.student.main;

import edu.brown.cs.student.main.Constants.Errors;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVParserTest {

  CSVParser<List<String>> csv;

  FileReader byRace;

  @Before
  public void setup() {
    try {
      byRace =
          new FileReader(
              "/Users/muhamedka/32/csv-MuhamedOnMars" + "/data/census/income_by_race.csv");
    } catch (FileNotFoundException e) {
      System.out.println(Errors.OPENERR.report() + e.getMessage());
    }
  }

  @Test
  public void parseTest() throws FactoryFailureException {
    StringReader reader = new StringReader("Apple, pie.,\"Well, I don't " + "think so.\"");
    csv = new CSVParser<>(new BufferedReader(reader), new StrListCreatorFromRow());
    List<String> row = csv.parse().get(0);
    Assert.assertEquals(3, row.size());
    Assert.assertEquals(Arrays.asList("Apple", " pie.", "\"Well, I don't think so.\""), row);
  }

  @Test
  public void parseNullTest() throws FactoryFailureException {
    StringReader reader = new StringReader("");
    csv = new CSVParser<>(new BufferedReader(reader), new StrListCreatorFromRow());
    Assert.assertEquals(Arrays.asList(), csv.parse());
  }

  @Test
  public void parseMalformedCSVTest() throws FactoryFailureException {
    // A test to check that malformed CSVs are correctly caught by the parser

  }

  @Test
  public void parseMismatchTest() throws FactoryFailureException {
    //
    // Wasn't able to implement this test, unfortunately
  }
}
