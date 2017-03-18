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
			st = connection.prepareStatement("select * from stock;");
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
