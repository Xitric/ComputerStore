/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kdavi16.dm505.computerstore.presentation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import kdavi16.dm505.computerstore.business.StoreMediator;

/**
 * FXML Controller class
 *
 * @author Kasper
 */
public class StoreFrontController implements Initializable {

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
		StoreMediator store = StoreMediator.getInstance();
	}
}
