
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Mediator for business logic. This mediator will set up a connection to the
 * database and forward method calls to the respective business classes.
 *
 * @author Kasper
 */
public class StoreMediator {

	/* Database connection constants */
	private static final String URL = "jdbc:postgresql://localhost:5432/";
//	private static final String URL = "jdbc:postgresql://localhost:5432/computerStore";
//	private static final String USER = "postgres";
//	private static final String PASSWORD = "1234";

	/**
	 * Singleton instance for the store mediator.
	 */
	private static StoreMediator instance;

	/**
	 * The connection to the database.
	 */
	private Connection connection;

	/**
	 * Object for handling various sql queries and updates
	 */
	private final StoreLogic storeLogic;

	/**
	 * Initialize the singleton. This creates a new singleton instance.
	 *
	 * @param dbName     the name of the database
	 * @param dbUser     the name of the database user
	 * @param dbPassword the password for the database
	 */
	public static void initialize(String dbName, String dbUser, String dbPassword) {
		instance = new StoreMediator(dbName, dbUser, dbPassword);
	}

	/**
	 * Get the singleton instance of the store mediator. This instance will give
	 * access to all relevant business logic. The singleton must be initialized
	 * first.
	 *
	 * @return the singleton instance of the store mediator
	 */
	public static StoreMediator getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Singleton must be initialized first!");
		}

		return instance;
	}

	/**
	 * Private mediator constructor.
	 *
	 * @param dbName     the name of the database
	 * @param dbUser     the name of the database user
	 * @param dbPassword the password for the database
	 */
	private StoreMediator(String dbName, String dbUser, String dbPassword) {
		establishConnection(dbName, dbUser, dbPassword);
		storeLogic = new StoreLogic();
	}

	/**
	 * Attmept to establish a connection to the database using the PostgreSQL
	 * driver with JDBC.
	 *
	 * @param dbName     the name of the database
	 * @param dbUser     the name of the database user
	 * @param dbPassword the password for the database
	 */
	private void establishConnection(String dbName, String dbUser, String dbPassword) {
		try {
			connection = DriverManager.getConnection(URL + dbName, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a view of all the components and their amounts in the database.
	 *
	 * @return a view of all the components and their amounts in the database
	 */
	public TableData listComponents() {
		return storeLogic.listComponents(connection);
	}

	/**
	 * Get a view of all components that must be restocked to reach their
	 * preferred amounts and how many of each must be bought.
	 *
	 * @return a view of all components that must be restocked
	 */
	public TableData listRestocking() {
		return storeLogic.listRestocking(connection);
	}

	/**
	 * Get a view of all registered systems and how many of each can be built
	 * from the current stock.
	 *
	 * @return a view of all registered systems and how many of each can be
	 *         built
	 */
	public TableData listSystems() {
		return storeLogic.listSystems(connection);
	}

	/**
	 * Get a view of all the components of the specified kind and their prices.
	 *
	 * @param kind the kind of component, in lowercase, or "all" to return all
	 *             components
	 * @return a view of all the components of the specified kind and their
	 *         prices
	 */
	public TableData listComponentPrices(String kind) {
		return storeLogic.listComponentPrices(connection, kind);
	}

	/**
	 * Get a view of all registered systems, their components and their selling
	 * prices. This will only return those systems that can be built from the
	 * current stock.
	 *
	 * @return a view of all registered systems, their components and their
	 *         selling prices
	 */
	public TableData listSystemPrices() {
		return storeLogic.listSystemPrices(connection);
	}

	/**
	 * Attempt to sell the specified quantity of the component with the
	 * specified name.
	 *
	 * @param name     the name of the component
	 * @param quantity the quantity to sell
	 * @return a string describing the result of calling this method. This
	 *         should be displayed to the user
	 */
	public String sellComponent(String name, int quantity) {
		return storeLogic.sellComponent(connection, name, quantity);
	}

	/**
	 * Attempt to sell the specified quantity of the system with the specified
	 * name.
	 *
	 * @param name     the name of the system
	 * @param quantity the quantity to sell
	 * @return a string describing the result of calling this method. This
	 *         should be displayed to the user
	 */
	public String sellSystem(String name, int quantity) {
		return storeLogic.sellSystem(connection, name, quantity);
	}

	/**
	 * Calculate the price offer for the specified system and quantity.
	 *
	 * @param name     the name of the system
	 * @param quantity the quantity of the system
	 * @return the price offer for the specified system and quantity
	 */
	public double priceOffer(String name, int quantity) {
		return storeLogic.priceOffer(connection, name, quantity);
	}

	/**
	 * Close and dispose all resources used by this store mediator.
	 */
	public void dispose() {
		//Attempt to close the connection
		DBUtil.close(connection);
	}
}
