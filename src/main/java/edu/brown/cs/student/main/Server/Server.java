package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.DataSource.CSVDataSource;
import edu.brown.cs.student.main.DataSource.GeneralCSVDataSource;
import edu.brown.cs.student.main.Parser.CSVParser;
import java.util.concurrent.TimeUnit;
import spark.Spark;

import java.util.List;

import static spark.Spark.after;

/**
 * Quick Summary:
 * Top-level class for the server application. Contains the main() method which starts Spark and runs the various handlers.
 * The OrderHandler takes in a state (menu) that can be shared if we extended the restaurant.
 * They need to share state (a menu). This would be a great opportunity to use dependency injection.
 * If we needed more endpoints, more functionality classes, etc., we could make sure they all had the same shared state.
 */

/**
 * Top-level class for the server application. Contains the main() method which starts Spark and runs the various handlers.
 */
public class Server {

  /**
   * Main method to start the server application.
   *
   * @param args - command-line arguments.
   */
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);
    /*
       Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
       be able to make requests to the server.

       By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
       This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
       from any website. Instead, you should set this header to the origin of your client, or a list of origins
       that you trust.

       By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
       Again, it's generally better to be more specific here and only allow the methods you need, but for
       this demo we'll allow all methods.

       We recommend you learn more about CORS with these resources:
           - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
           - https://portswigger.net/web-security/cors
    */
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    // setting up data source needed for the handlers
    CSVDataSource source = new GeneralCSVDataSource();
    System.out.println(source);

    // defining caching parameters for the ACS datasource
    int size = 100; // maximum size of the cache
    long expireAfterWriteDuration = 30; // time duration after which entries expire
    TimeUnit timeUnit = TimeUnit.MINUTES; // time unit for the expiration duration
    ACSDatasource acsDatasource = new ACSDatasource(size, expireAfterWriteDuration, timeUnit);
    // setting up Spark handlers for various endpoints
    Spark.get("loadcsv", new LoadCSVHandler(source));
    Spark.get("viewcsv", new ViewCSVHandler(source));
    Spark.get("searchcsv", new SearchCSVHandler(source));
    Spark.get("broadband", new BroadbandHandler(acsDatasource));
    // initialize Spark and await initialization
    Spark.init();
    Spark.awaitInitialization();
    // print server started message
    System.out.println("Server started at http://localhost:" + port);
  }
}