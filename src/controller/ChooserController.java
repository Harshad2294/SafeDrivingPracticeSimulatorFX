package controller;

import model.Demo;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * This is a controller class to choose 'Automated Demo' or 'Practice' version
 * @author Harshad Shettigar
 * @version 1.0
 */
public class ChooserController {

    //class variables
    @FXML
    private ToggleGroup demoChoice;
    @FXML
    private RadioButton demoRadio;
    @FXML
    private RadioButton practiceRadio;

    /**
     * This method is used to initialize window to select 'demo' or 'practice'
     */
    @FXML
    public void initialize() {
        demoRadio.setUserData("D");
        practiceRadio.setUserData("P");
        if (Demo.demo >= 3) {
            practiceRadio.setSelected(true);
            demoRadio.setDisable(true);
        }
    }

    /**
     * This method is used to load simulation window
     */
    @FXML
    private void loadSimulation() {
        try {
            Stage primaryStage;
            primaryStage = new Stage();
            if (((demoChoice.getSelectedToggle().getUserData().toString()).equals("D")) && Demo.demo < 3) {
                Demo.demo++;
                Demo.demoSelected = true;
                primaryStage.setTitle("Safe-Driving Simulator (Automated Demo)");
            } else {
                primaryStage.setTitle("Safe-Driving Simulator");
            }
            Parent root = FXMLLoader.load(getClass().getResource("/view/SimulationView.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            Platform.runLater(primaryStage::show);
            Stage stage = ((Stage) demoRadio.getScene().getWindow());
            stage.close();
        } catch (IOException ex) {
        }
    }
}
