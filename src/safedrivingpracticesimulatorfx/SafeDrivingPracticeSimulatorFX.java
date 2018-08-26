package safedrivingpracticesimulatorfx;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is a main class which is used to start the simulation
 * @author Harshad Shettigar
 * @version 1.0
 */
public class SafeDrivingPracticeSimulatorFX extends Application {

    /**
     * This method acts like a 'main' method of a traditional Java program
     * As this is a JavaFX application, there is no need for a 'main' method
     * @param primaryStage Stage to display JavaFX application
     */
    @Override
    public void start(Stage primaryStage) throws IOException  {
        loadLogin();
    }

    /**
     * Method to load login screen
     */
    private void loadLogin() throws IOException {
        Stage primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginView.fxml"));
        Scene scene = new Scene(root);
        primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
	public static void main(String[] args) {
		launch(args);
	}
}
