/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.shared;

/**
 * Interface describing an implementation independent object storing the results
 * of a database query.
 *
 * @author Kasper
 */
public interface TableData {

	/**
	 * Get the name of the column with the specified index.
	 *
	 * @param col the column index
	 * @return the name of the column with the specified index.
	 */
	String getColumnName(int col);

	/**
	 * Get the value in the specified row and column.
	 *
	 * @param row the row index
	 * @param col the column index
	 * @return the value in the specified row and column
	 */
	Object getValue(int row, int col);

	/**
	 * Get the amount of columns.
	 *
	 * @return the amount of columns
	 */
	int getColumnCount();

	/**
	 * Get the amount of rows.
	 *
	 * @return the amount of rows
	 */
	int getRowCount();
}
