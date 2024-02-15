package edu.brown.cs.student.main.DataSource;

import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.util.List;

/**
 * Quick Summary:
 * Defines methods for interacting with a CSV data source.
 * Provides functionality to retrieve the current CSV matrix and set a new CSV matrix.
 */

/**
 * Defines methods for interacting with a new CSV data source.
 */
public interface CSVDataSource {

    /**
     * Retrieves the current CSV matrix.
     *
     * @return - the current CSV matrix.
     */
    CSVData getCurrentMatrix();

    /**
     * Sets the current CSV matrix to the specified filepath.
     *
     * @param filepath - the filepath of the new CSV matrix.
     * @throws DatasourceException - if an error occurs while setting the new CSV matrix.
     */
    void setCurrentMatrix(List<List<String>> filepath) throws DatasourceException;
}