package edu.brown.cs.student.main.Search;

import edu.brown.cs.student.main.Exceptions.ColumnConversionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UtilitySearch {

  public enum Options {
    NONE,
    HEADER,
    CASE_SEN,
    MTCH_LOCK,
    ;
  }

  public List<List<String>> mtrx;

  public List<Integer> matchedRows;

  public HashMap<String, Integer> map;
  public Options[] options;

  public int rows;

  public int cols;

  /**
   * @param mtrx, options
   */
  public UtilitySearch(List<List<String>> mtrx, Options[] options) {
    this.mtrx = mtrx;
    this.options = options;
    this.rows = mtrx.size();
    this.matchedRows = new ArrayList<>();

    if (rows != 0) this.cols = mtrx.get(0).size();
    else this.cols = 0;

    if (options[0] == Options.HEADER) {
      this.map = new HashMap<>();
      for (int x = 0; x < cols; x++) {
        map.put(mtrx.get(0).get(x), x);
      }
    } else {
      this.map = null;
    }
  }

  public UtilitySearch(List<List<String>> mtrx) {
    this(mtrx, new Options[] {Options.NONE, Options.NONE, Options.NONE});
  }

  public int search(String value, String colStr) {
    int col;

    try {
      col = convertStringToInt(colStr);
    } catch (ColumnConversionException e) {
      System.err.println("Error when converting column: " + e.getMessage());
      return -1;
    }

    for (int row = options[0].ordinal(); row < rows; row++) {
      if (match(mtrx.get(row).get(col), value) && !this.mat  mchedRows.contains(row)) {
        this.matchedRows.add(row);
        return row;
      }
    }
    return -1;
  }

  public boolean match(String toMatch, String value) {
    String match = toMatch;
    String val = value;
    if (options[1] != Options.CASE_SEN) {
      match = match.toLowerCase();
      val = val.toLowerCase();
    }

    if (options[2] == Options.MTCH_LOCK) {
      return match.equals(val);
    } else {
      return match.contains(val);
    }
  }

  public int search(String value) {
    for (int row = options[0].ordinal(); row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (match(mtrx.get(row).get(col), value) && !this.matchedRows.contains(row)) {
          this.matchedRows.add(row);
          return row;
        }
      }
    }
    return -1;
  }

  /**
   * Given a String str that represents a valid column index, maps the str to a valid column index
   * and returns it. When the HEADER Option is specified, then the function will additionally accept
   * strings which contain column names as valid inputs.
   *
   * @param str, a valid column index
   * @return Either: The corresponding column index to str. -1 if the column name or index inputted
   *     does not map to a valid column name.
   * @throws ColumnConversionException - an exception that occurs if there was an error in
   *     processing the column identifier
   */
  public int convertStringToInt(String str) throws ColumnConversionException {
    Integer col;
    try {
      col = Integer.parseInt(str);
      if (col >= cols || col < 0) throw new ColumnConversionException("Invalid" + " column index");
    } catch (NumberFormatException e) {
      if (options[0] != Options.HEADER) {
        throw new ColumnConversionException("Column name inputted when headers were not specified");
      }
      col = map.get(str);
      if (col == null) throw new ColumnConversionException("Invalid column name");
    }
    return col;
  }
}
