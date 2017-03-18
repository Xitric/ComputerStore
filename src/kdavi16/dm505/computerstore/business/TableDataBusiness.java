/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.business;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import kdavi16.dm505.computerstore.shared.TableData;

/**
 * Business implementation of the {@link TableData} interface. This
 * implementation allows filling a TableData object with information.
 *
 * @author Kasper
 */
class TableDataBusiness implements TableData {

	private final int columnCount;
	private final String[] columnNames;
	private final List<Object[]> rows;

	/**
	 * Constructs a new table data object with the specified number of columns.
	 *
	 * @param columnCount the amount of columns in the table
	 */
	public TableDataBusiness(int columnCount) {
		this.columnCount = columnCount;
		this.columnNames = new String[columnCount];
		this.rows = new ArrayList<>();
	}

	/**
	 * Constructs a new table data object with the data from the specified
	 * result set.
	 *
	 * @param rs the result set to acquire data from
	 * @throws SQLException if an error occurs while extracting information
	 */
	public TableDataBusiness(ResultSet rs) throws SQLException {
		//Extract column data
		this(rs.getMetaData().getColumnCount());
		for (int i = 0; i < columnCount; i++) {
			columnNames[i] = rs.getMetaData().getColumnLabel(i + 1);
		}

		//Extract row data
		while (rs.next()) {
			Object[] newRow = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				if (rs.getMetaData().getColumnType(i + 1) == Types.CHAR) {
					newRow[i] = rs.getString(i + 1).trim(); //Trim to remove padding spaces
				} else {
					newRow[i] = rs.getObject(i + 1);
				}
			}
			rows.add(newRow);
		}
	}

	/**
	 * Set the name of the specified column.
	 *
	 * @param col  the column index
	 * @param name the name of the column
	 */
	public void setColumnName(int col, String name) {
		this.columnNames[col] = name;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Add the specified values to this table as a new row. If the amount of
	 * values exceed the return value of {@link #getColumnCount()}, the extra
	 * values will be discarded.
	 *
	 * @param values the values to add
	 */
	public void addRow(String... values) {
		String[] newRow = new String[columnCount];
		System.arraycopy(values, 0, newRow, 0, columnCount);
		rows.add(newRow);
	}

	@Override
	public Object getValue(int row, int col) {
		return rows.get(row)[col];
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}
}
