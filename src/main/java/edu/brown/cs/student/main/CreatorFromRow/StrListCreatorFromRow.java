package edu.brown.cs.student.main.CreatorFromRow;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

/**
 * Quick Summary:
 * Implements a strategy for creating lists of strings from rows in a CSV file.
 * Provides a method to create a list of strings from a list of strings representing a row.
 */

/**
 * Implements a strategy for creating lists of strings from rows in a CSV file.
 */
public class StrListCreatorFromRow implements CreatorFromRow<List<String>> {

  /**
   * Constructs a StrListCreatorFromRow
   */
  public StrListCreatorFromRow() {}

  /**
   * Creates a list of strings from the specified row.
   *
   * @param row - the list of strings representing the row.
   * @return - the created list of strings.
   * @throws FactoryFailureException - if an error occurs during object creation.
   */
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
