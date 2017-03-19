/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.business;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import kdavi16.dm505.computerstore.shared.TableData;

/**
 * Class for handling SQL queries on the database used for monitoring the
 * current stock of components and systems.
 *
 * @author Kasper
 */
class PartStorage {

	/**
	 * Get a view of all the components and their amounts in the database.
	 *
	 * @param connection the database connection
	 * @return a view of all the components and their amounts in the database
	 */
	public TableData listComponents(Connection connection) {
		PreparedStatement st = null;
		ResultSet rs = null;
		TableDataBusiness table = null;

		try {
			st = connection.prepareStatement("select name as component, amount from stock;");
			rs = st.executeQuery();
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
	 * Get a view of all registered systems and how many of each can be built
	 * from the current stock.
	 *
	 * @param connection the database connection
	 * @return a view of all registered systems and how many of each can be
	 *         built
	 */
	public TableData listSystems(Connection connection) {
		PreparedStatement st = null;
		ResultSet rs = null;
		TableDataBusiness table = null;

		try {
			st = connection.prepareStatement("select name as system, min(amount) as available from ("
					+ "select System.name, amount from System, Stock where"
					+ " Stock.name = cpuName or"
					+ "	Stock.name = ramName or"
					+ "	Stock.name = caseName or"
					+ "	Stock.name = gpuName or"
					+ "	Stock.name = mainboardName"
					+ ") limiting "
					+ "group by name;");
			rs = st.executeQuery();
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
	 * Get a view of all the components and their prices ordered by kind.
	 *
	 * @param connection the database connection
	 * @return a view of all the components and their prices ordered by kind
	 */
	public TableData listComponentPrices(Connection connection) {
		PreparedStatement st = null;
		ResultSet rs = null;
		TableDataBusiness table = null;

		try {
			st = connection.prepareStatement("select name, kind, price from component order by kind;");
			rs = st.executeQuery();
			table = new TableDataBusiness(rs);
			
			//The above query returns the base prices, so we must calculate the
			//business prices for the components. This is 1.3 times the base
			//price
			int priceCol = table.getColumnIndex("price");
			for (int i = 0; i < table.getRowCount(); i++) {
				//Get the base price
				double basePrice = ((BigDecimal) table.getValue(i, priceCol)).doubleValue();
				
				//Calculate business price
				double businessPrice = basePrice * 1.3;
				table.setValue(i, priceCol, businessPrice);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(st);
			DBUtil.close(rs);
		}

		return table;
	}

	/**
	 * Get a view of all the components of the specified kind and their prices.
	 *
	 * @param connection the database connection
	 * @param kind       the kind of component, in lowercase
	 * @return a view of all the components of the specified kind and their
	 *         prices
	 */
	public TableData listComponentPrices(Connection connection, String kind) {
		PreparedStatement st = null;
		ResultSet rs = null;
		TableDataBusiness table = null;

		try {
			st = connection.prepareStatement("select name, price from component where kind = ?;");
			st.setString(1, kind);
			rs = st.executeQuery();
			table = new TableDataBusiness(rs);
			
			//The above query returns the base prices, so we must calculate the
			//business prices for the components. This is 1.3 times the base
			//price
			int priceCol = table.getColumnIndex("price");
			for (int i = 0; i < table.getRowCount(); i++) {
				//Get the base price
				double basePrice = ((BigDecimal) table.getValue(i, priceCol)).doubleValue();
				
				//Calculate business price
				double businessPrice = basePrice * 1.3;
				table.setValue(i, priceCol, businessPrice);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(st);
			DBUtil.close(rs);
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
		PreparedStatement st = null;
		ResultSet rs = null;
		TableDataBusiness table = null;

		try {
			st = connection.prepareStatement("select o.name, cpuName, ramName, caseName, gpuName, mainboardName, ("
					+ "(select price from Component, System i where i.name = o.name and Component.name = cpuName)"
					+ "+"
					+ "(select price from Component, System i where i.name = o.name and Component.name = ramName)"
					+ "+"
					+ "(select price from Component, System i where i.name = o.name and Component.name = caseName)"
					+ "+"
					+ "(select price from Component, System i where i.name = o.name and Component.name = gpuName)"
					+ "+"
					+ "(select price from Component, System i where i.name = o.name and Component.name = mainboardName)"
					+ ") as price from System o, ("
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
			rs = st.executeQuery();
			table = new TableDataBusiness(rs);
			
			//The above query returns the base prices, so we must calculate the
			//business prices for the systems. This is 1.3 times the base price
			//rounded up to the next number ending in 99
			int priceCol = table.getColumnIndex("price");
			for (int i = 0; i < table.getRowCount(); i++) {
				//Get the base price
				double basePrice = ((BigDecimal) table.getValue(i, priceCol)).doubleValue();
				
				//Calculate business price
				double businessPrice = Math.ceil((basePrice * 1.3) / 100.0) * 100.0 - 1;
				table.setValue(i, priceCol, businessPrice);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(st);
			DBUtil.close(rs);
		}

		return table;
	}
}
