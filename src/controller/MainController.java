package controller;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.LoginModel;

/**
 * This is a controller class for Login window
 * @author Harshad Shettigar
 * @version 1.0
 */
public class MainController {

    //class variables
    @FXML
    private VBox carVBox;
    @FXML
    private TextField username;
    @FXML
    private TextField password;

    private LoginModel login;

    /**
     * This method is used to initialize window for Login
     */
    @FXML
    public void initialize() {
        login = new LoginModel();
    }

    /**
     * This method is used to clear text fields
     */
    @FXML
    private void clear() {
        username.setText("");
        password.setText("");
    }

    /**
     * This method is used to verify login details
     */
    @FXML
    private void login() {
        String result = login.validate(username.getText(), password.getText());
        if (result != null) {
            Platform.runLater(this::loadChooser);
        } else {
            JOptionPane.showMessageDialog(null, "Please check credentials again.");
        }
    }

    /**
     * This method is used to load window to select 'demo' or 'practice'
     */
    private void loadChooser() {
        try {
            Stage primaryStage;
            Parent root = FXMLLoader.load(getClass().getResource("/view/ChooserView.fxml"));
            Scene scene = new Scene(root);
            primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            Stage stage = ((Stage) username.getScene().getWindow());
            stage.close();
        } catch (IOException ex) {
        }
    }
}
