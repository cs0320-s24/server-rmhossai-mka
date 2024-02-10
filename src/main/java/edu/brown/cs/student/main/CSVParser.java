package edu.brown.cs.student.main;

import edu.brown.cs.student.main.Constants.Errors;
import edu.brown.cs.student.main.CreatorFromRow.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVParser<T> {

  public BufferedReader reader;

  public CreatorFromRow<T> strat;

  /* REGEX CHECK:
   I'm aware that this regex doesn't handle single quotes well.
  */

  static final String regex = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))";

  public CSVParser(Reader reader, CreatorFromRow<T> strat) {
    this.reader = new BufferedReader(reader);
    this.strat = strat;
  }

  // String value, int id
  public List<T> parse() throws FactoryFailureException {
    // A FactoryFailureExpression is thrown to be handled by the caller, which may have a more
    // informative error message.

    int rowSize;
    int expectedRowSize = Integer.MAX_VALUE;

    List<T> mtrx = new ArrayList<>();
    String buf;
    try {
      while ((buf = reader.readLine()) != null) {
        List<String> splitLine = Arrays.asList(buf.split(regex));
        mtrx.add(strat.create(splitLine));
        rowSize = splitLine.size();
        if (expectedRowSize == Integer.MAX_VALUE) expectedRowSize = rowSize;
        if (rowSize != expectedRowSize) {
          System.err.println(
              "Warning: Columns are not equal! If you intend "
                  + "to search, the search may break!");
        }
      }
      reader.close();
    } catch (IOException e) {
      System.err.println(Errors.IOERR.report() + e);
    }
    return mtrx;
  }
}
