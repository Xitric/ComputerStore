/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.business;

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
		TableData table = null;

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
		TableData table = null;

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
		TableData table = null;

		try {
			st = connection.prepareStatement("select name, kind, price from component order by kind;");
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
	 * Get a view of all the components of the specified kind and their prices.
	 *
	 * @param connection the database connection
	 * @param kind the kind of component, in lowercase
	 * @return a view of all the components of the specified kind and their
	 *         prices
	 */
	public TableData listComponentPrices(Connection connection, String kind) {
		PreparedStatement st = null;
		ResultSet rs = null;
		TableData table = null;

		try {
			st = connection.prepareStatement("select name, price from component where kind = ?;");
			st.setString(1, kind);
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
}
