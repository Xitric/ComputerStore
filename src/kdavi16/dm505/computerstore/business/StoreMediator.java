/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import kdavi16.dm505.computerstore.shared.TableData;

/**
 * Mediator for business logic. This mediator will set up a connection to the
 * database and forward method calls to the respective business classes.
 *
 * @author Kasper
 */
public class StoreMediator {

	/* Database connection constants */
	private static final String URL = "jdbc:postgresql://localhost:5432/computerStore";
	private static final String USER = "postgres";
	private static final String PASSWORD = "1234";

	/**
	 * Singleton instance for the store mediator.
	 */
	private static StoreMediator instance;

	/**
	 * The connection to the database.
	 */
	private Connection connection;

	/* Objects for handling various sql queries and updates */
	private final PartStorage partStorage;

	/**
	 * Get the singleton instance of the store mediator. This instance will give
	 * access to all relevant business logic.
	 *
	 * @return the singleton instance of the store mediator
	 */
	public static StoreMediator getInstance() {
		//Lazily initialize store mediator
		if (instance == null) {
			instance = new StoreMediator();
		}

		return instance;
	}

	/**
	 * Private mediator constructor.
	 */
	private StoreMediator() {
		//TODO: Handle connection error
		establishConnection();

		partStorage = new PartStorage();
	}

	/**
	 * Attmept to establish a connection to the database using the PostgreSQL
	 * driver with JDBC.
	 *
	 * @return {@code true} if a connection was established, {@code false}
	 *         otherwise
	 */
	private boolean establishConnection() {
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Get a view of all the components and their amounts in the database.
	 *
	 * @return a view of all the components and their amounts in the database
	 */
	public TableData listComponents() {
		return partStorage.listComponents(connection);
	}

	/**
	 * Get a view of all registered systems and how many of each can be built
	 * from the current stock.
	 *
	 * @return a view of all registered systems and how many of each can be
	 *         built
	 */
	public TableData listSystems() {
		return partStorage.listSystems(connection);
	}

	/**
	 * Get a view of all the components and their prices ordered by kind.
	 *
	 * @return a view of all the components and their prices ordered by kind
	 */
	public TableData listComponentPrices() {
		return partStorage.listComponentPrices(connection);
	}
	
	/**
	 * Get a view of all the components of the specified kind and their prices.
	 *
	 * @param kind the kind of component, in lowercase
	 * @return a view of all the components of the specified kind and their
	 *         prices
	 */
	public TableData listComponentPrices(String kind) {
		return partStorage.listComponentPrices(connection, kind);
	}

	/**
	 * Close and dispose all resources used by this store mediator.
	 */
	public void dispose() {
		//Attempt to close the connection
		DBUtil.close(connection);
	}
}
