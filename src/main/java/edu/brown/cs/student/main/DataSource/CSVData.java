package edu.brown.cs.student.main.DataSource;

/**
 * Quick Summary:
 * Represents CSV data in the form of a matrix.
 * Stores a matrix of strings representing CSV data.
 */

import java.util.List;

/**
 * Represents CSV data in the form of a matrix.
 * @param mtrx - the matrix to set as the current CSV matrix.
 */
public record CSVData(List<List<String>> mtrx) {

}

