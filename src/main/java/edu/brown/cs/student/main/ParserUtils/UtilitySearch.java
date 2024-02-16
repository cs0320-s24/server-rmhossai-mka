package edu.brown.cs.student.main.ParserUtils;

import edu.brown.cs.student.main.Exceptions.ColumnConversionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Quick Summary:
 * Provides utility methods for searching data matrices.
 * Supports searching by specific column or across all columns.
 * Allows customization of search options such as case sensitivity and exact matching.
 */

/**
 * Provides utility methods for searching data matrices.
 */
public class UtilitySearch {

  // enum defining search options
  public enum Options {
    NONE,
    HEADER,
    CASE_SEN,
    MTCH_LOCK,
    ;
  }

  // data fields
  public List<List<String>> mtrx; // data matrix to be searched
  public List<Integer> matchedRows; // rows that have been matched
  public HashMap<String, Integer> map; // maps column names to indices if headers are specified
  public Options[] options; // search options
  public int rows; // number of rows in the matrix
  public int cols; // number of columns in the matrix

  /**
   * Constructs a UtilitySearch object with the specified data matrix and search options.
   *
   * @param mtrx - the data matrix to be searched.
   * @param options - the search options to be applied.
   */
  public UtilitySearch(List<List<String>> mtrx, Options[] options) {
    this.mtrx = mtrx;
    this.options = options;
    this.rows = mtrx.size();
    this.matchedRows = new ArrayList<>();
    // initializes column count, handle headers if specified
    if (rows != 0) this.cols = mtrx.get(0).size();
    else this.cols = 0;

    if (options[0] == Options.HEADER) {
      // initializes map if headers are specified
      this.map = new HashMap<>();
      for (int x = 0; x < cols; x++) {
        map.put(mtrx.get(0).get(x), x);
      }
    } else {
      this.map = null;
    }
  }

  /**
   * Constructs a UtilitySearch object with the specified data matrix and default search options.
   *
   * @param mtrx - the data matrix to be searched.
   */
  public UtilitySearch(List<List<String>> mtrx) {
    // call the constructor with default options
    this(mtrx, new Options[] {Options.NONE, Options.NONE, Options.NONE});
  }

  /**
   * Searches for the specified value within the specified column of the data matrix.
   *
   * @param value - the value to search for.
   * @param colStr - the column identifier (either index or name) to search within.
   * @return - the row index where the value is found, or -1 if not found.
   */
  public int search(String value, String colStr) {
    int col;
    try {
      col = convertStringToInt(colStr); // convert column string to integer index
    } catch (ColumnConversionException e) {
      System.err.println("Error when converting column: " + e.getMessage());
      return -1;
    }
    // iterate through rows and check for matches
    for (int row = options[0].ordinal(); row < rows; row++) {
      if (match(mtrx.get(row).get(col), value) && !this.matchedRows.contains(row)) {
        this.matchedRows.add(row);
        return row;
      }
    }
    return -1;
  }

  /**
   * Determines if a given value matches the target value based on search options.
   *
   * @param toMatch - the value to match against.
   * @param value - the target value to match.
   * @return - true if a match is found, false otherwise.
   */
  public boolean match(String toMatch, String value) {
    String match = toMatch;
    String val = value;
    if (options[1] != Options.CASE_SEN) {
      match = match.toLowerCase(); // convert to lowercase if case sensitivity is disabled
      val = val.toLowerCase();
    }
    // check for exact match or substring match based on search option
    if (options[2] == Options.MTCH_LOCK) {
      return match.equals(val);
    } else {
      return match.contains(val);
    }
  }

  /**
   * Searches for the specified value across all columns of the data matrix.
   *
   * @param value - the value to search for.
   * @return - the row index where the value is found, or -1 if not found.
   */
  public int search(String value) {
    // iterate through all rows and columns and check for matches
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
   * Converts a column identifier (index or name) to its corresponding integer representation.
   *
   * @param str - the column identifier
   * @return - the corresponding column index or -1 of the column name or index inputted has an invalid map.
   * @throws ColumnConversionException - if there was an error processing the column identifier.
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