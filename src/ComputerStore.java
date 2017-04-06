
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the computer store application.
 *
 * @author Kasper
 */
public class ComputerStore extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		//Initialize JavaFX application
		Parent root = FXMLLoader.load(getClass().getResource("StoreFront.fxml"));

		Scene scene = new Scene(root);

		stage.setTitle("Computer Store");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		//We better close all connections before exiting
		StoreMediator.getInstance().dispose();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
