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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kdavi16.dm505.computerstore.business.StoreMediator;
import kdavi16.dm505.computerstore.shared.TableData;

/**
 * FXML Controller class
 *
 * @author Kasper
 */
public class StoreFrontController implements Initializable {

	@FXML
	private ComboBox<String> componentSelector;
	@FXML
	private TableView<List<Object>> printTable;
	@FXML
	private TableView<List<Object>> purchaseComponentTable;
	@FXML
	private TableView<List<Object>> purchaseSystemTable;
	@FXML
	private TextField componentQuantityField;
	@FXML
	private TextField systemQuantityField;

	private Alert alertDialog;

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

		//Alert dialog
		alertDialog = new Alert(AlertType.INFORMATION);
		alertDialog.setTitle("Error");

	}

	/**
	 * Fill out the result table with the data from a {@link TableData} object.
	 *
	 * @param data  the object containing the data to display
	 * @param table the table to present the data in
	 */
	private void present(TableData data, TableView<List<Object>> table) {
		//Reset table
		table.getColumns().clear();
		table.getItems().clear();

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

			table.getColumns().add(column);
		}

		//Create rows. Each row consists of a list of objects
		for (int i = 0; i < data.getRowCount(); i++) {
			List<Object> row = new ArrayList<>();

			for (int j = 0; j < data.getColumnCount(); j++) {
				row.add(data.getValue(i, j));
			}

			table.getItems().add(row);
		}
	}

	@FXML
	private void listStockOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listComponents();
		present(data, printTable);
	}

	@FXML
	private void listComputersOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listSystems();
		present(data, printTable);
	}

	@FXML
	private void listComponentPricesOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listComponentPrices(componentSelector.getValue().toLowerCase());
		present(data, printTable);
	}

	@FXML
	private void listComputerPricesOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listSystemPrices();
		present(data, printTable);
	}

	@FXML
	private void refreshComponentListOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listComponentPrices("all");
		present(data, purchaseComponentTable);
	}

	@FXML
	private void purchaseComponentOnAction(ActionEvent event) {
		//Get the name of the selected component, if any
		List<Object> selection = purchaseComponentTable.getSelectionModel().getSelectedItem();
		if (selection == null) {
			alertDialog.setHeaderText("Purchase error");
			alertDialog.setContentText("You must select a component in the table!");
			alertDialog.showAndWait();
			return;
		}

		String componentName = (String) selection.get(0);

		//Get the desired quantity, if any
		int quantity = 0;
		try {
			quantity = Integer.parseInt(componentQuantityField.getText());
		} catch (NumberFormatException e) {
			alertDialog.setHeaderText("Purchase error");
			alertDialog.setContentText("You must specify a valid quantity in the text field!");
			alertDialog.showAndWait();
			return;
		}

		//Component and quantity successfully selected, initiate purchase
		String result = StoreMediator.getInstance().sellComponent(componentName, quantity);
		alertDialog.setHeaderText("Purchase result");
		alertDialog.setContentText(result);
		alertDialog.showAndWait();

		//Refresh table
		refreshComponentListOnAction(null);
	}

	@FXML
	private void refreshSystemListOnAction(ActionEvent event) {
		TableData data = StoreMediator.getInstance().listSystemPrices();
		present(data, purchaseSystemTable);
	}

	@FXML
	private void purchaseSystemOnAction(ActionEvent event) {
	}
}
