package edu.brown.cs.student.main.DataSource;

import java.util.List;

public interface CSVDataSource {
    CSVData getCurrentMatrix();

    void setCurrentMatrix(List<List<String>> filepath) throws DatasourceException;
}
