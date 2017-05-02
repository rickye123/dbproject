package smartstreamdb;

/* 
  The Read class is used from the config.properties file 
  and the two .data files. It also includes JUnit testing for 
  the relevant methods.  
*/

import java.util.*; 
import java.io.*;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Read {
	
	private List<List<String>> tableFromFile = new ArrayList<List<String>>(); 

    /**
     * getter method for the tableFromFile 2D array list 
     * @return 2D array list of Strings
     */
    public List<List<String>> getTableFromFile() {
        return tableFromFile; 
    }

    /**
     * reads file line by line and adds it to arraylist tableFromFile
     * @param filename - the file to be read from, e.g. "Person.data"
     */
    public void readFromDataFile(String filename, String controlCharacter) {

        try {
            File file = new File(filename);
            Scanner read = new Scanner(file);
            int row = 0; 
      
            // while still lines to be read 
            while(read.hasNext()) {
                String line = read.nextLine(); 

                if(!line.isEmpty()) {
                
                	// split the String into an array of Strings using 
                	// the control character
                	String[] record = line.split(controlCharacter);
            
                	tableFromFile.add(new ArrayList<String>());

                	// add each String in record to tableFromFile
                	// skipping out any empty fields
                	for (String field : record) {
                		if(!field.isEmpty() || field != null) {
                			tableFromFile.get(row).add(field);
                		}
                	}
                	row++;
                }
	    	}
            read.close(); // close file 
	    } catch(FileNotFoundException readException) {
	    	throw new Error(readException);
	    }
	}

    /**
     * read from config file (.properties) to get the database, 
     * user and password so that a connection can be established
     * @param filename - the config file as a string 
     * @return - HashMap containing the database, user, and password
     */
	public Map<String, String> readFromConfigFile(String filename) {
	    
		Properties prop = new Properties();
		InputStream input = null; 

		Map<String, String> field = new HashMap<String, String>();
		
		try {
			input = new FileInputStream(filename);
			
			// load a properties file 
			prop.load(input);	

		    field.put("database", prop.getProperty("database"));
		    field.put("dbuser", prop.getProperty("dbuser"));
		    field.put("dbpassword", prop.getProperty("dbpassword"));
		} catch(IOException readException) {
			throw new Error(readException);
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException readException) {
					readException.printStackTrace();
				}
			}
		}
		return field;
		
	}
    
	public static void main(String[] args) {

        Read tests = new Read(); 
        tests.testing();

	}
    
    public void testing() {
    	testReadFromDataFile();
    	testReadFromConfigFile();
        System.out.println("All Tests Passed");
    }
    
    @Test
	private void testReadFromDataFile() {

		readFromDataFile("Test.data", "\\|");
		assertEquals("Test ID", tableFromFile.get(0).get(0));
		assertEquals("10", tableFromFile.get(1).get(0));
		assertEquals("Test Number", tableFromFile.get(0).get(1));
		assertEquals("Test Name", tableFromFile.get(0).get(2));
		assertEquals("2001", tableFromFile.get(2).get(1));
		assertEquals("Test1", tableFromFile.get(3).get(2));
		
		clearTable(); // clear contents of table
		
	}

    @Test
    private void testReadFromConfigFile() {
    	
    	Map<String, String> testVariables = readFromConfigFile("testConfig.properties");
    	assertEquals("localhost:3306/testDatabase", testVariables.get("database"));
    	assertEquals("password", testVariables.get("dbpassword"));
    	assertEquals("userName", testVariables.get("dbuser"));
    	
    }
    
    // clear the entire table - used for unit testing 
    private void clearTable() {

        while (!tableFromFile.isEmpty()) {
            int size = tableFromFile.size();
            int i = 0; 
            while (i < size) {
            	tableFromFile.remove(0);
                i++;
            } 
        }
    }
    
}