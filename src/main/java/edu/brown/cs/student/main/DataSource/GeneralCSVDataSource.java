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

public class GeneralCSVDataSource implements CSVDataSource {
    private CSVData csvData;

    public GeneralCSVDataSource() {
        this.csvData = null;
    }
    public CSVData getCurrentMatrix() {
        return this.csvData;
    }

    public void setCurrentMatrix(List<List<String>> mtrx)  {
        this.csvData = new CSVData(mtrx);
    }
}
