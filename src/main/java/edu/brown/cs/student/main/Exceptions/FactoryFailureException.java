package edu.brown.cs.student.main.Exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick Summary:
 * Represents an exception related to factory failure, typically when creating an object from a row.
 * Provides information about the error message and the row involved in the failure.
 */

/**
 * Represents an exception related to factory failure, typically when creating an object from a row.
 */
public class FactoryFailureException extends Exception {
  final List<String> row; // the row involved in the factory failure

  /**
   * Constructs a FactoryFailureException with the specified error message and row.
   *
   * @param message - the error message.
   * @param row - the row involved in the factory failure.
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
