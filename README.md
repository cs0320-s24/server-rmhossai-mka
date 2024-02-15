> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Detail

# Design Choices

# Errors/Bugs

# Tests

# How to

# Outline


DONE SO FAR:

LoadCSVHandler
ViewCSVHandler
SearchCSVHandler

NEED TO DO:
UtilitySearch cleanup
Server cleanup
StrListCreatorFromRow cleanup
CSVDataSource cleanup
DatasourceException cleanup
GeneralCSVDataSource cleanup
BroadbandHandler cleanup

GenCSVDataSourceProxy class
complete ACSDataSource class

Integration and Unit Testing


1) Start the code using CSV Parser
- Add the CSV Parser Java files to this project
- Create a new Server.java class for the main method to start the server
- Import the necessary SparkJava classes like Spark.staticFiles and Spark.exceptionHandler
- Add code to the main() method to start the server on a specific port (e.g. Spark.port(4567))

2) Implement User Story 1 to load, view, search CSV files
- Add API endpoints
  - Create CSVHandler.java class that implements the Route interface
  - Add @GET annotated methods for /loadcsv, /viewcsv, /searchcsv endpoints
- Load CSV file
  - In loadcsv() method, get the filepath parameter
  - Use the CSV parser to load the file at that path into a data structure
- View CSV contents
  - In viewcsv() method, return the loaded CSV data as JSON
- Search CSV
  - Implement searching on the loaded CSV similarly to the previous sprint
  - Return matching rows as JSON from the searchcsv() method

3) Implement User Story 2 for broadband data
- Create BroadbandHandler class with a @GET /broadband method
- Get the state and county parameters
- Make a request to the ACS API to get the broadband percentage
- Return the result and parameters as JSON

4) Implement caching and configuration for ACS data
- Create an ACSDatasource class to encapsulate the ACS API logic
- Use Guava caching to cache responses based on request parameters
- Expose methods to configure the cache (size, expiration)
- Allow the developer to configure aspects of the cache like size and eviction policy 
- Identify which configuration options the developer should control vs what is managed internally

5) Add integration and unit tests
- Create test classes for the server endpoints and CSV/ACS classes
- Use mocks for ACS data to avoid live API calls in unit tests
- Write tests that make live requests to test full integration

6) Add documentation and defensive coding practices
- Annotate classes and methods with JavaDoc comments
- Handle errors and exceptions efficiently, with accurate error messages
- Validate all user-provided inputs


/* SearchCSVHandler

*/

/* ViewCSVHandler

*/

/* CSVParser (upgraded)

*/