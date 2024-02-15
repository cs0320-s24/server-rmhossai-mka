package edu.brown.cs.student.main.Parser;

import edu.brown.cs.student.main.Constants.Errors;
import edu.brown.cs.student.main.CreatorFromRow.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Quick Summary:
 * A CSV parser that reads from a Reader and creates objects using a CreatorFromRow strategy.
 * The parser reads each line from the Reader and splits it into a list of strings using a regular expression.
 * The parser then uses the CreatorFromRow strategy to create an object from each line.
 * The parser can handle lines with different numbers of columns, but it will print a warning if the number of
 * columns is not a equal for all likes.
 * The parser throws a FactoryFailureException if the CreatorFromRow strategy fails to create an object.
 */

/**
 * A CSV parser that reads from a Reader and creates objects using a CreatorFromRow strategy.
 *
 * @param <T> - the type of objects created by the parser.
 */
public class CSVParser<T> {

  // the reader from which the parser reads
  private BufferedReader reader;
  // the strategy used by the parser to create objects from rows.
  private CreatorFromRow<T> strat;
  // the regular expression used by the parser to create objects from rows.
  static final String regex = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))";

  /**
   * Creates a new CSV parser that reads from the specified reader and uses the specified strategy to create objects.
   *
   * @param reader - the reader from which the parser reads.
   * @param strat - the strategy used by the parser to create objects from rows.
   */
  public CSVParser(Reader reader, CreatorFromRow<T> strat) {
    this.reader = new BufferedReader(reader);
    this.strat = strat;
  }

  /**
   * Constructs a CSVParser with null reader and strategy.
   */
  public CSVParser() {
    this.reader = null;
    this.strat = null;
  }

  /**
   * Parses the input and creates a list of objects.
   * The parser reads each line from the reader and splits it into a list of strings using the regex.
   * The parser then uses the CreatorFromRow strategy to create an object from each line.
   * If the number of columns is not equal for all lines, the parser will print a warning.
   * If the CreatorFromRow strategy fails to create an object, the parser will throw a FactoryFailureException.
   *
   * @return - a list of objects created by the parser.
   * @throws FactoryFailureException - if the CreatorFromRow strategy fails to create an object.
   */
  public List<T> parse() throws FactoryFailureException {
    if (this.reader == null || this.strat == null) {
      System.err.println("Either reader or parse strategy uninitialized. " + "Please use /load to load in a CSV file");
      return new ArrayList<>();
    }
    // the size of the current row
    int rowSize;
    // the expected size of each row
    int expectedRowSize = Integer.MAX_VALUE;

    // the list of objects created by the parser
    List<T> mtrx = new ArrayList<>();
    // the current line read from the reader
    String buf;
    // read each line from the reader and split it into a list of strings
    try {
      while ((buf = reader.readLine()) != null) {
        List<String> splitLine = Arrays.asList(buf.split(regex));
        mtrx.add(strat.create(splitLine));
        rowSize = splitLine.size();
        if (expectedRowSize == Integer.MAX_VALUE) expectedRowSize = rowSize;
        if (rowSize != expectedRowSize) {
          System.err.println(
              "Warning: Columns are not equal! If you intend " + "to search, the search may break!");
        }
      }
      reader.close();
    } catch (IOException e) {
      System.err.println(Errors.IOERR.report() + e);
    }
    return mtrx;
  }
}