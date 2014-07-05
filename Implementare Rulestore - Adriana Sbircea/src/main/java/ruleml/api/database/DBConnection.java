package ruleml.api.database;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * Class that initializes the database connection.
 *  
 * @author Adriana
 */
public class DBConnection {
	/**
	 * The database driver.
	 */
	final String driver = "org.exist.xmldb.DatabaseImpl";

	/**
	 * The instance for the {@link DBConnection} class.
	 */
	private static DBConnection instance = null;
	
	/**
	 * Constructor.
	 */
	private DBConnection() {
		// Create the connection to the database
		try {
			// Initialize database driver
			Class cl = Class.forName(driver);
			// Get instance to the database
			Database database = (Database) cl.newInstance();
			database.setProperty("create-database", "true");
			// Register the database
			DatabaseManager.registerDatabase(database);
		} catch (ClassNotFoundException e) {
			// TODO
			e.printStackTrace();
		} catch (XMLDBException e) {
			// TODO
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO
			e.printStackTrace();
		}
	
	}
	
	/**
	 * Creates the database connection if the connection was not already used.
	 */
	public static void initConnection() {
		if (instance == null) {
			instance = new DBConnection();
		}
	}
}
