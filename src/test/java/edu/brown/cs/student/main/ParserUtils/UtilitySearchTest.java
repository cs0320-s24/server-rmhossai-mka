package edu.brown.cs.student.main.ParserUtils;

import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import edu.brown.cs.student.main.Exceptions.ColumnConversionException;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.ParserUtils.CSVParser;
import edu.brown.cs.student.main.ParserUtils.UtilitySearch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class UtilitySearchTest {

  List<List<String>> mtrx;

  CSVParser<List<String>> csv;

  UtilitySearch search;

  @Before
  public void setup() throws FactoryFailureException {
    FileReader reader = null;
    try {
      reader = new FileReader("data/census/postsecondary_education.csv");
    } catch (FileNotFoundException e) {
      System.err.println("Test file could not be opened.");
      System.exit(0);
    }

    csv = new CSVParser<>(new BufferedReader(reader), new StrListCreatorFromRow());
    mtrx = csv.parse();
    search =
        new UtilitySearch(
            mtrx,
            new UtilitySearch.Options[] {
              UtilitySearch.Options.HEADER,
              UtilitySearch.Options.CASE_SEN,
              UtilitySearch.Options.MTCH_LOCK
            });
  }

  @Test
  public void searchTest() {
    // Successful column search tests, including searches with a column name identifier over an
    // index
    Assert.assertEquals(6, search.search("American Indian or Alaska Native", "IPEDS Race"));
    Assert.assertEquals(1, search.search("Brown University", "University"));
    Assert.assertEquals(9, search.search("Women", "8"));
    Assert.assertEquals(7, search.search("Non-resident Alien", "0"));
  }

  @Test
  public void searchInvalidColTest() {
    // Multiple invalid column tests, including column names that are not present as headers
    Assert.assertEquals(-1, search.search("American Indian or Alaska Native", "105"));
    Assert.assertEquals(-1, search.search("American Indian or Alaska Native", "-1"));
    Assert.assertEquals(-1, search.search("American Indian or Alaska Native", "WantsApplePie"));
    Assert.assertEquals(-1, search.search("American Indian or Alaska Native", "WantsMoreApplePie"));
  }

  @Test
  public void searchNoMatchTest() {
    // Multiple no match tests, where given a valid column, the requested value is not found at all.
    Assert.assertEquals(-1, search.search("Of Arab Descent", "10"));
    Assert.assertEquals(-1, search.search("", "8"));

    // Includes incorrect column searches.
    Assert.assertEquals(-1, search.search("brown-university", "University"));
    Assert.assertEquals(-1, search.search("0.069233258", "Slug University"));
  }

  @Test
  public void searchAllTest() {
    // Search tests where no column is specified; simply searches all cols for the value
    Assert.assertEquals(1, search.search("217156"));
    Assert.assertEquals(10, search.search("95"));
    Assert.assertEquals(9, search.search("2"));
    Assert.assertEquals(12, search.search("207"));
  }

  @Test
  public void searchAllNoMatchTest() {
    // Search all tests where there is no match in the entire CSV
    Assert.assertEquals(-1, search.search(""));
    Assert.assertEquals(-1, search.search("#@%@%@%#!!!"));
    Assert.assertEquals(
        -1,
        search.search(
            "When you have eliminated the impossible, whatever remains, "
                + "however improbable, must be the truth."));
    Assert.assertEquals(-1, search.search("I love 32!"));
  }

  @Test
  public void matchTest() {
    // Testing match for where there are successful matches, and case sensitivity and exact match
    // are turned on
    Assert.assertTrue(search.match("\"Salagadoola\"", "\"Salagadoola\""));
    Assert.assertTrue(search.match("Mechikaboola\r", "Mechikaboola\r"));
    Assert.assertTrue(search.match("Bibidi\r", "Bibidi\r"));
    Assert.assertTrue(search.match("Bobbidi ", "Bobbidi "));
    Assert.assertTrue(search.match("\tBoo", "\tBoo"));
  }

  @Test
  public void matchNotCaseSensitiveTest() {
    // Testing match where the option for case sensitivity is removed.
    search =
        new UtilitySearch(
            mtrx,
            new UtilitySearch.Options[] {
              UtilitySearch.Options.HEADER,
              UtilitySearch.Options.NONE,
              UtilitySearch.Options.MTCH_LOCK
            });
    Assert.assertTrue(search.match("sit on a potato pan, otis.", "Sit on a potato pan, Otis."));
    Assert.assertTrue(
        search.match(
            "cigar? toss it in a can. it is so tragic.\r",
            "Cigar? Toss it in a can. It is so tragic.\r"));
    Assert.assertTrue(
        search.match(
            "go hang a salami, i'm a lasagna hog.", "Go hang a salami, I'm a lasagna hog."));
  }

  @Test
  public void matchNotExactTest() throws FactoryFailureException {
    // Testing match where the option for an exact value search is removed.
    search =
        new UtilitySearch(
            new ArrayList<>(),
            new UtilitySearch.Options[] {
              UtilitySearch.Options.HEADER,
              UtilitySearch.Options.CASE_SEN,
              UtilitySearch.Options.NONE
            });
    Assert.assertTrue(search.match("\"Elementary, my dear Watson.\"", "\"Elementary"));
    Assert.assertTrue(search.match("علوم الكملبوتر", "الكملبوتر"));
    Assert.assertTrue(search.match("There is a hidden message in here", "s a h"));
  }

  @Test
  public void noMatchTest() {
    // Match tests with case sensitivity and exact match triggered
    Assert.assertFalse(search.match("There is no hidden message in here", ""));
    Assert.assertFalse(search.match("NoBlanksPlease", ""));

    // Match tests with only exact match triggered
    search =
        new UtilitySearch(
            new ArrayList<>(),
            new UtilitySearch.Options[] {
              UtilitySearch.Options.HEADER,
              UtilitySearch.Options.NONE,
              UtilitySearch.Options.MTCH_LOCK
            });
    Assert.assertFalse(search.match("There is no hidden message in here", "Hidden message"));
    Assert.assertFalse(search.match("nocapsplease", "CapsPlease"));

    // Match tests with only case-sensitive match triggered
    search =
        new UtilitySearch(
            new ArrayList<>(),
            new UtilitySearch.Options[] {
              UtilitySearch.Options.HEADER,
              UtilitySearch.Options.CASE_SEN,
              UtilitySearch.Options.NONE
            });
    Assert.assertFalse(search.match("There is no hidden message in here", "no hidden message?"));
    Assert.assertFalse(search.match("NoErrorsPlease", "ErorrsPlease"));

    // Match tests with neither triggered
    search =
        new UtilitySearch(
            new ArrayList<>(),
            new UtilitySearch.Options[] {
              UtilitySearch.Options.HEADER, UtilitySearch.Options.NONE, UtilitySearch.Options.NONE
            });
    Assert.assertFalse(search.match("There is no hidden message in here", "None"));
    Assert.assertFalse(search.match("NoErrorsPlease", "ErorrsPlease"));
  }

  @Test
  public void convertValidColIndexTest() throws ColumnConversionException {
    // Conversion tests where a valid column index was inputted
    Assert.assertEquals(2, search.convertStringToInt("2"));
    Assert.assertEquals(0, search.convertStringToInt("0"));
    Assert.assertEquals(9, search.convertStringToInt("9"));
    Assert.assertEquals(5, search.convertStringToInt("5"));
  }

  @Test
  public void convertValidColNameTest() throws ColumnConversionException {
    // Conversion tests where a valid column name was inputted
    Assert.assertEquals(5, search.convertStringToInt("Completions"));
    Assert.assertEquals(4, search.convertStringToInt("University"));
  }

  @Test(expected = ColumnConversionException.class)
  public void convertInvalidColInputTestA() throws ColumnConversionException {
    search.convertStringToInt("-1421");
  }

  @Test(expected = ColumnConversionException.class)
  public void convertInvalidColInputTestB() throws ColumnConversionException {
    search.convertStringToInt("-1");
  }

  @Test(expected = ColumnConversionException.class)
  public void convertInvalidColInputTestC() throws ColumnConversionException {
    search.convertStringToInt("10");
  }

  @Test(expected = ColumnConversionException.class)
  public void convertColNameNoHeaderTestA() throws ColumnConversionException {
    search =
        new UtilitySearch(
            mtrx,
            new UtilitySearch.Options[] {
              UtilitySearch.Options.NONE,
              UtilitySearch.Options.CASE_SEN,
              UtilitySearch.Options.MTCH_LOCK
            });
    search.convertStringToInt("ID Year");
  }

  @Test(expected = ColumnConversionException.class)
  public void convertColNameNoHeaderTestB() throws ColumnConversionException {
    search =
        new UtilitySearch(
            mtrx,
            new UtilitySearch.Options[] {
              UtilitySearch.Options.NONE,
              UtilitySearch.Options.CASE_SEN,
              UtilitySearch.Options.MTCH_LOCK
            });
    search.convertStringToInt("IPEDS Race");
  }

  @Test(expected = ColumnConversionException.class)
  public void convertColNameNoHeaderTestC() throws ColumnConversionException {
    search =
        new UtilitySearch(
            mtrx,
            new UtilitySearch.Options[] {
              UtilitySearch.Options.NONE,
              UtilitySearch.Options.CASE_SEN,
              UtilitySearch.Options.MTCH_LOCK
            });
    search.convertStringToInt("Year");
  }
}
