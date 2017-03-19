/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.presentation;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kdavi16.dm505.computerstore.business.StoreMediator;
import kdavi16.dm505.computerstore.shared.TableData;

/**
 * FXML Controller class
 *
 * @author Kasper
 */
public class StoreFrontController implements Initializable {

	@FXML
	private TableView<List<Object>> resultTable;
	@FXML
	private ComboBox<String> componentSelector;

	/**
	 * Initializes the controller class
	 *
	 * @param url the location used to resolve relative paths for the root
	 *            object, or null if the location is not known
	 * @param rb  the resources used to localize the root object, or null if the
	 *            root object was not localized
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		componentSelector.getItems().addAll("All", "CPU", "RAM", "GPU", "Case", "Mainboard");
		componentSelector.getSelectionModel().select(0);
	}

	/**
	 * Fill out the result table with the data from a {@link TableData} object.
	 *
	 * @param data the object containing the data to display
	 */
	private void present(TableData data) {
		//Reset table
		resultTable.getColumns().clear();
		resultTable.getItems().clear();

		//Create columns
		for (int i = 0; i < data.getColumnCount(); i++) {
			TableColumn<List<Object>, Object> column = new TableColumn<>(data.getColumnName(i));

			//The first column should be styled specially
			if (i == 0) {
				column.setId("first-column");
			}

			//We need to set our own cell value factory to get the table data to
			//show up, since we are creating a dynamic table.
			column.setCellValueFactory(param -> {
				//Get the index of this table column
				int index = param.getTableView().getColumns().indexOf(param.getTableColumn());

				//Get the objects on the current row
				List<Object> row = (List<Object>) param.getValue();

				//Return the value in the current column and row, returning null
				//if we exceed the boundaries of the list on this row. This
				//should not happen, but better safe than sorry
				return new SimpleObjectProperty(row.size() > index ? row.get(index) : null);
			});

			//We want to display doubles with only two decimals
			column.setCellFactory(param -> new TableCell<List<Object>, Object>() {
				@Override
				protected void updateItem(Object value, boolean empty) {
					super.updateItem(value, empty);
					if (empty) {
						setText(null);
					} else if (value instanceof Double) {
						setText(String.format("%.2f", (Double) value));
					} else {
						setText(value.toString());
					}
				}
			});

			resultTable.getColumns().add(column);
		}

		//Create rows. Each row consists of a list of objects
		for (int i = 0; i < data.getRowCount(); i++) {
			List<Object> row = new ArrayList<>();

			for (int j = 0; j < data.getColumnCount(); j++) {
				row.add(data.getValue(i, j));
			}

			resultTable.getItems().add(row);
		}
	}

	@FXML
	private void listStockOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listComponents();
		present(data);
	}

	@FXML
	private void listComputersOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listSystems();
		present(data);
	}

	@FXML
	private void listComponentPricesOnAction(ActionEvent event) {
		TableData data;

		//Depending on the user's selection, we will either print all kinds or
		//only one
		if ("All".equals(componentSelector.getValue())) {
			data = StoreMediator.getInstance().listComponentPrices();
		} else {
			data = StoreMediator.getInstance().listComponentPrices(componentSelector.getValue().toLowerCase());
		}

		present(data);
	}

	@FXML
	private void listComputerPricesOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listSystemPrices();
		present(data);
	}
}
