package edu.brown.cs.student.main.CreatorFromRow;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

/**
 * Quick Summary:
 * Defines a strategy for creating objects from rows in a CSV file.
 * Provides a method to create an object of type T from a list of strings representing a row.
 */

/**
 * Defines a strategy for creating objects from rows in a CSV file.
 * @param <T> - the type of object to create from each row.
 */
public interface CreatorFromRow<T> {

  /**
   * Creates an object of type T from the specified row.
   *
   * @param row - the list of strings representing the row.
   * @return - the created object.
   * @throws FactoryFailureException - if an error occurs during object creation.
   */
  T create(List<String> row) throws FactoryFailureException;
}