package edu.brown.cs.student.main.DataSource;

import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.util.List;

public class GenCSVDataSourceProxy implements CSVDataSource {

    private final CSVDataSource source;

    public GenCSVDataSourceProxy(CSVDataSource source) {
        this.source = source;
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
