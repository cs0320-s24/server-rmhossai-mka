package edu.brown.cs.student.main.CreatorFromRow;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

public class StrListCreatorFromRow implements CreatorFromRow<List<String>> {
  public StrListCreatorFromRow() {}

  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
