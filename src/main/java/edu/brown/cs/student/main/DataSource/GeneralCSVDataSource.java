package edu.brown.cs.student.main.DataSource;

import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Parser.CSVParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Quick Summary:
 * Represents a general CSV data source.
 * Provides methods to retrieve the current CSV matrix and set a new CSV matrix.
 */

/**
 * Represents a general CSV data source.
 */
public class GeneralCSVDataSource implements CSVDataSource {
    private CSVData csvData; // current CSV data

    /**
     * Constructs a GeneralCSVDataSource with null CSV data.
     */
    public GeneralCSVDataSource() {
        this.csvData = null;
    }

    /**
     * Retrieves the current CSV matrix.
     *
     * @return - the current CSV matrix.
     */
    public CSVData getCurrentMatrix() {
        return this.csvData;
    }

    /**
     * Sets the current CSV matrix to the specified matrix.
     *
     * @param mtrx - the matrix to set as the current CSV matrix.
     */
    public void setCurrentMatrix(List<List<String>> mtrx)  {
        this.csvData = new CSVData(mtrx);
    }
}
