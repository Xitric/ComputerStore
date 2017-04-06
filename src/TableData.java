
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
	 * Get the index of the column with the specified name.
	 *
	 * @param name the name of the column
	 * @return the index of the column with the specified name
	 */
	int getColumnIndex(String name);

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
