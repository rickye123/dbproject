package smartstreamdb;

/* This is the main control class for the program */

import java.util.*;
import java.sql.Connection;

public class Program {

    private Display display = new Display();
    private Connector database = new Connector();
	private List<List<String>> table = new ArrayList<List<String>>(); 
    private String controlCharacter; 
    private Execute sqlQuery;
    private Read read = new Read();

    /**
     * set the controlCharacter variable 
     * @param controlCharacter - a string that is used as the separator
     * between fields 
     */
	public void setControlCharacter(String controlCharacter) {
	    this.controlCharacter = controlCharacter; 
	}

	/**
	 * getter method to get the control character 
	 * @return - returns the controlCharacter string 
	 */
    public String getControlCharacter() {
        return controlCharacter; 
    }

    /**
     * getter method to return the array list table
     * @return - 2D array list of strings called table 
     */
    public List<List<String>> getTable() {
        return table; 
    }

    /**
     * clear the table 
     */
    public void clearTable() {

        while (!table.isEmpty()) {
            int size = table.size();
            int i = 0; 
            while (i < size) {
                table.remove(0);
                i++;
            } 
        }
    }

    /**
     * Creates a read object and reads a file, splitting the strings by the 
     * control character. The resulting 2D array list is copied into table
     * which is then added to the database using the insert method in Execute()
     * @param filename - the file to be opened, e.g. "Person.data"; 
     * @param control - the control character used to split up strings in file, e.g. "," or "|"
     * @param header - array list of strings containing the columns, e.g. "ORDER_ID, ORDER_NUM..."
     * @param name - the table name, e.g. "PERSON"
     */
    public void addToDatabase(String filename, String control, List<String> header, String name) {

        read.readFromDataFile(filename, control);

        table = read.getTableFromFile(); 
        
        sqlQuery.insert(table, header, name); // insert into database 

        clearTable(); // clear contents of table

    }

    /**
     * puts a variable length of String variables into an array list of Strings
     * @param strings - variable length of strings, acting as the columns for the table
     * @return an array list of these Strings
     */
    public List<String> createHeader(String ... columns) {

        if(columns.length == 0) {
            throw new Error("Must have at least one argument");
        }

        List<String> header = new ArrayList<String>();

        for (String column : columns) {
            header.add(column);
        }

        return header; 

    }
    
    public static void main(String[] args) {

        Program prog = new Program();
        prog.run();
    }

    // run the program 
    public void run() {

        
    	database.establishJDBCDriver(); // see if JDBC class exists
    	
    	// read the connection string, password, and user from the config file 
    	Map<String, String> connectionVariables = read.readFromConfigFile("config.properties");	
    	
    	database.open(connectionVariables); // open database
        
        Connection connection = database.getConnection(); // get database connection string 
        sqlQuery = new Execute(connection);
        
        // insert information from "Person.data"
        setControlCharacter(","); // Person.data is separated by a "," character
        List<String> personHeader = createHeader("PERSON_ID, FIRST_NAME, LAST_NAME, STREET, CITY");
        addToDatabase("Person.data", controlCharacter, personHeader, "PERSON");

        // insert information from "Order.data"
        setControlCharacter("\\|"); // the "|" character is used in regex, so this needs to be escaped 
        List<String> orderHeader = createHeader("ORDER_ID, ORDER_NUMBER, PERSON_ID");
        // ORDER is a keyword in SQL, so this needs to be escaped using the "`" characters
        addToDatabase("Order.data", controlCharacter, orderHeader, "`ORDER`");

        // execute queries 
        executeQueries();
                
        // deletes rows added - used for debugging
        deleteRowsFromDatabase();

        database.close(); // close database 

    }
    
    /**
     * Execute SQL queries - print persons with at least one order 
     * and print all orders with first name of corresponding person 
     */
    private void executeQueries() {
     
        // Persons with at least one order 
        List<List<String>> oneOrder = sqlQuery.getPersonsWithOneOrder();
        display.printTable("Persons with at least one order", oneOrder);
        
        // All orders with first name of corresponding person (if available) 
        List<List<String>> ordersWithFirstName = sqlQuery.getOrdersWhenFirstNameAvailable();
        display.printTable("All orders with first name of corresponding person", ordersWithFirstName);
    
    }
    
    /**
     * deletes the rows added to the database - used to debug the program
     * and remove the rows added from the files
     */
    public void deleteRowsFromDatabase() {
    	
        sqlQuery.deleteRows("`ORDER`", "ORDER_ID", "10");
        sqlQuery.deleteRows("`ORDER`", "ORDER_ID", "11");
        sqlQuery.deleteRows("`ORDER`", "ORDER_ID", "12");
        sqlQuery.deleteRows("`ORDER`", "ORDER_ID", "13");
        sqlQuery.deleteRows("PERSON", "PERSON_ID", "1");
        sqlQuery.deleteRows("PERSON", "PERSON_ID", "2");
        sqlQuery.deleteRows("PERSON", "PERSON_ID", "3"); 
    	
    }

}
