/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.business;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import kdavi16.dm505.computerstore.shared.TableData;

/**
 * Single class to contain all business logic, including performing sql queries,
 * calculating price offers and selling components and systems.
 *
 * @author Kasper
 */
public class StoreLogic {

	/**
	 * The factor by which components' base prices are multiplied when
	 * calculating business prices.
	 */
	public static final double SELL_FACTOR = 1.3;

	/**
	 * Safely run the specified query and receive the result.
	 *
	 * @param connection the database connection
	 * @param query      the query to run
	 * @return the result of the query
	 */
	private TableDataBusiness runQuery(Connection connection, String query) {
		Statement st = null;
		ResultSet rs = null;
		TableDataBusiness table = null;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			table = new TableDataBusiness(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(st);
			DBUtil.close(rs);
		}

		return table;
	}

	/**
	 * Safely run the specified update.
	 *
	 * @param connection the database connection
	 * @param update     the update to run
	 */
	private void runUpdate(Connection connection, String update) {
		Statement st = null;

		try {
			st = connection.createStatement();
			st.executeUpdate(update);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(st);
		}
	}

	/**
	 * Get a view of all the components and their amounts in the database.
	 *
	 * @param connection the database connection
	 * @return a view of all the components and their amounts in the database
	 */
	public TableData listComponents(Connection connection) {
		return runQuery(connection,
				"select name as component, amount from stock;");
	}

	/**
	 * Get a view of all registered systems and how many of each can be built
	 * from the current stock.
	 *
	 * @param connection the database connection
	 * @return a view of all registered systems and how many of each can be
	 *         built
	 */
	public TableData listSystems(Connection connection) {
		return runQuery(connection,
				"select name as system, min(amount) as available from ("
				+ "select System.name, amount from System, Stock where"
				+ " Stock.name = cpuName or"
				+ "	Stock.name = ramName or"
				+ "	Stock.name = caseName or"
				+ "	Stock.name = gpuName or"
				+ "	Stock.name = mainboardName"
				+ ") limiting "
				+ "group by name;");
	}

	/**
	 * Get a view of all the components of the specified kind and their prices.
	 *
	 * @param connection the database connection
	 * @param kind       the kind of component, in lowercase, or "all" to return
	 *                   all components
	 * @return a view of all the components of the specified kind and their
	 *         prices
	 */
	public TableData listComponentPrices(Connection connection, String kind) {
		TableDataBusiness table;

		if ("all".equals(kind)) {
			table = runQuery(connection,
					"select Component.name, kind, price, amount from Component, Stock where Component.name = Stock.name order by kind;");
		} else {
			table = runQuery(connection,
					"select Component.name, price, amount from Component, Stock where Component.name = Stock.name and kind = '" + kind + "';");
		}

		//The above queries return the base prices, so we must calculate the
		//business prices for the components. This is 1.3 times the base
		//price
		int priceCol = table.getColumnIndex("price");
		for (int i = 0; i < table.getRowCount(); i++) {
			//Get the base price
			double basePrice = ((BigDecimal) table.getValue(i, priceCol)).doubleValue();

			//Calculate business price
			double businessPrice = basePrice * SELL_FACTOR;
			table.setValue(i, priceCol, businessPrice);
		}

		return table;
	}

	/**
	 * Get a view of all registered systems, their components and their selling
	 * prices. This will only return those systems that can be built from the
	 * current stock.
	 *
	 * @param connection the database connection
	 * @return a view of all registered systems, their components and their
	 *         selling prices
	 */
	public TableData listSystemPrices(Connection connection) {
		TableDataBusiness table = runQuery(connection,
				"select o.name, cpuName, ramName, caseName, gpuName, mainboardName, ("
				+ "(select price from Component, System i where i.name = o.name and Component.name = cpuName)"
				+ "+"
				+ "(select price from Component, System i where i.name = o.name and Component.name = ramName)"
				+ "+"
				+ "(select price from Component, System i where i.name = o.name and Component.name = caseName)"
				+ "+"
				+ "(select price from Component, System i where i.name = o.name and Component.name = gpuName)"
				+ "+"
				+ "(select price from Component, System i where i.name = o.name and Component.name = mainboardName)"
				+ ") as price, availability from System o, ("
				+ "select name, min(amount) as availability from ("
				+ "		select System.name, amount from System, Stock where"
				+ "			Stock.name = cpuName or"
				+ "			Stock.name = ramName or"
				+ "			Stock.name = caseName or"
				+ "			Stock.name = gpuName or"
				+ "			Stock.name = mainboardName"
				+ "		) limiting "
				+ "group by name"
				+ ") storage "
				+ "where storage.name = o.name and availability > 0;");

		//The above query returns the base prices, so we must calculate the
		//business prices for the systems. This is 1.3 times the base price
		//rounded up to the next number ending in 99
		int priceCol = table.getColumnIndex("price");
		for (int i = 0; i < table.getRowCount(); i++) {
			//Get the base price
			double basePrice = ((BigDecimal) table.getValue(i, priceCol)).doubleValue();

			//Calculate business price
			double businessPrice = Math.ceil((basePrice * SELL_FACTOR) / 100.0) * 100.0 - 1;
			table.setValue(i, priceCol, businessPrice);
		}

		return table;
	}

	/**
	 * Attempt to sell the specified quantity of the component with the
	 * specified name.
	 *
	 * @param connection the database connection
	 * @param name       the name of the component
	 * @param quantity   the quantity to sell
	 * @return a string describing the result of calling this method. This
	 *         should be displayed to the user
	 */
	public String sellComponent(Connection connection, String name, int quantity) {
		Statement st = null;

		try {
			st = connection.createStatement();
			int changed = st.executeUpdate("update Stock set amount = amount - " + quantity + " where name = '" + name + "';");

			if (changed == 0) {
				return "Unknown component: " + name;
			}
		} catch (SQLException e) {
			return "Error purchasing " + quantity + " of the component " + name;
		} finally {
			DBUtil.close(st);
		}

		return "Successfully purchased " + quantity + " of the component " + name;
	}

	/**
	 * Attempt to sell the specified quantity of the system with the specified
	 * name.
	 *
	 * @param connection the database connection
	 * @param name       the name of the system
	 * @param quantity   the quantity to sell
	 * @return a string describing the result of calling this method. This
	 *         should be displayed to the user
	 */
	public String sellSystem(Connection connection, String name, int quantity) {
		Statement st = null;

		try {
			//Stop auto committing. This is necessary if we run into an
			//exception in the middle of updating our stock numbers
			connection.setAutoCommit(false);
			st = connection.createStatement();

			//Update quantities of all components in the system
			st.executeUpdate("update Stock set amount = amount - " + quantity + " where name = ("
					+ "select cpuName from System where name = '" + name + "'"
					+ ");");
			st.executeUpdate("update Stock set amount = amount - " + quantity + " where name = ("
					+ "select ramName from System where name = '" + name + "'"
					+ ");");
			st.executeUpdate("update Stock set amount = amount - " + quantity + " where name = ("
					+ "select caseName from System where name = '" + name + "'"
					+ ");");
			st.executeUpdate("update Stock set amount = amount - " + quantity + " where name = ("
					+ "select gpuName from System where name = '" + name + "'"
					+ ");");
			st.executeUpdate("update Stock set amount = amount - " + quantity + " where name = ("
					+ "select mainboardName from System where name = '" + name + "'"
					+ ");");

			//We made it through and can safely commit
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
				return "Error purchasing " + quantity + " of the system " + name;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		} finally {
			DBUtil.close(st);
		}

		return "Successfully purchased " + quantity + " of the system " + name;
	}

	/**
	 * Calculate the price offer for the specified system and quantity.
	 *
	 * @param connection the database connection
	 * @param name       the name of the system
	 * @param quantity   the quantity of the system
	 * @return the price offer for the specified system and quantity
	 */
	public double priceOffer(Connection connection, String name, int quantity) {
		//Get the base price for the system
		TableDataBusiness table = runQuery(connection,
				"select ("
				+ "(select price from Component, System where System.name = '" + name + "' and Component.name = cpuName)"
				+ "+"
				+ "(select price from Component, System where System.name = '" + name + "' and Component.name = ramName)"
				+ "+"
				+ "(select price from Component, System where System.name = '" + name + "' and Component.name = caseName)"
				+ "+"
				+ "(select price from Component, System where System.name = '" + name + "' and Component.name = gpuName)"
				+ "+"
				+ "(select price from Component, System where System.name = '" + name + "' and Component.name = mainboardName)"
				+ ") as price;");

		if (table.getRowCount() == 0) {
			throw new IllegalArgumentException("No such system " + name);
		}

		double basePrice = ((BigDecimal) table.getValue(0, 0)).doubleValue();

		//Calculate business price
		double businessPrice = Math.ceil((basePrice * SELL_FACTOR) / 100.0) * 100.0 - 1;

		//Calculate price offer
		double reduction = (quantity - 1) * 0.02;
		reduction = (reduction > 0.2 ? 0.2 : reduction);
		return businessPrice * (1 - reduction) * quantity;
	}
}
