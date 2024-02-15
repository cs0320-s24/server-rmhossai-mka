package edu.brown.cs.student.main.Exceptions;

/**
 * Quick Summary:
 * Represents an exception related to column conversion in UtilitySearch.
 * Indicates an error when attempting to convert a column string into an invalid index.
 */

/**
 * Represents an exception related to column conversion in UtilitySearch.
 */
public class ColumnConversionException extends Exception {

  /**
   * Constructs a ColumnConversionException with the specified error message.
   * @param message - the error message.
   */
  public ColumnConversionException(String message) {
    super(message);
  }
}
