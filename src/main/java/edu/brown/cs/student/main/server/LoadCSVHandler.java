package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.CSVParser;
import edu.brown.cs.student.main.CreatorFromRow.StrListCreatorFromRow;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class LoadCSVHandler implements Route {

    CSVParser<List<String>> parser;

    public LoadCSVHandler(CSVParser parser){
        this.parser = parser;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String csvFilePath = request.queryParams("loadCSV");
        FileReader reader = null;
        try {
             reader = new FileReader(csvFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("error");
        }
        parser = new CSVParser<>(new BufferedReader(reader),
                new StrListCreatorFromRow());
        return parser;
    }
}
