package edu.brown.cs.student.main.DataSource;

import javax.sql.DataSource;
import java.util.List;

public class GenCSVDataSourceProxy implements CSVDataSource {

    CSVDataSource source;
    public GenCSVDataSourceProxy () {

    }


    @Override
    public CSVData getCurrentMatrix() {
        return null;
    }

    @Override
    public void setCurrentMatrix(List<List<String>> filepath) throws DatasourceException {
        throw new UnsupportedOperationException("Cannot set matrix");
    }
}
