package edu.brown.cs.student.main.Constants;

/**
 * Quick Summary:
 * Defines a set of error messages used throughout the application.
 * Each error message is associated with an error code for easy identification.
 */

/**
 * Defines a set of error messages used throughout the application.
 */
public enum Errors {
  // error related to input/output operations
  IOERR("Error occurred while processing input stream: "),
  // error related to file opening
  OPENERR("Error occurred while trying to open file: "),
  // error related to incorrect command-line arguments
  ARGERR_MAIN("Incorrect number of arguments! Format: "),
  ;
  final String msg; // error message

  /**
   * Constructs an Errors enum with the specified error message.
   * @param msg - the error message.
   */
  Errors(String msg) {
    this.msg = msg;
  }

  /**
   * Returns the error message associated with this error.
   *
   * @return - the error message.
   */
  public String report() {
    return this.msg;
  }
}
