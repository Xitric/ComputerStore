/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.business;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class containing static utility methods.
 *
 * @author Kasper
 */
class DBUtil {

	/**
	 * Close the specified connection
	 *
	 * @param conn the connection to close
	 */
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				System.out.println("Successfully closed connection");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Close the specified statement
	 *
	 * @param st the statement to close
	 */
	public static void close(Statement st) {
		if (st != null) {
			try {
				st.close();
				System.out.println("Successfully closed statement");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Close the specified result set
	 *
	 * @param rs the result set to close
	 */
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				System.out.println("Successfully closed resultset");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
