package edu.brown.cs.student.main.Constants;

public enum Errors {
  IOERR("Error occurred while processing input stream: "),

  OPENERR("Error occurred while trying to open file: "),

  ARGERR_MAIN("Incorrect number of arguments! Format: "),
  ;
  final String msg;

  Errors(String msg) {
    this.msg = msg;
  }

  public String report() {
    return this.msg;
  }
}
