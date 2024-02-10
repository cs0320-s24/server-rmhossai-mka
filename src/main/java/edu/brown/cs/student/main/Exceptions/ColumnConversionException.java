package edu.brown.cs.student.main.Exceptions;

/**
 * This is an error provided to catch any error that may occur when there is an attempt to convert a
 * column string in UtilitySearch into an invalid index.
 */
public class ColumnConversionException extends Exception {

  public ColumnConversionException(String message) {
    super(message);
  }
}
