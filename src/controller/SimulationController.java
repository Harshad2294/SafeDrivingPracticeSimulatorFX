package controller;

import model.Demo;
import java.io.IOException;
import java.util.Random;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.SimulationModel;
import view.SimulationPane;

/**
 * This is a controller class for Simulation window
 * @author Harshad Shettigar
 * @version 1.0
 */
public class SimulationController {

    //class variables
    @FXML
    private MenuBar menuBar;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ComboBox carNumbers;
    @FXML
    private ComboBox carColors;
    @FXML
    private VBox durationBox;
    @FXML
    private Slider durationSlider;
    @FXML
    private Label durationLabel;
    @FXML
    private CheckBox schoolHours;
    @FXML
    private CheckBox roadRepair;
    @FXML
    private GridPane buttonGrid;
    @FXML
    private VBox timerVBox;
    @FXML
    private VBox centerBox;
    @FXML
    private Label timeLeft;

    private final ObservableList<String> carNoOptions = FXCollections.observableArrayList(
            "4",
            "5",
            "6",
            "7",
            "8"
    );
    private final ObservableList<String> carColOptions = FXCollections.observableArrayList(
            "Orange",
            "Olive Green",
            "Green",
            "Cyan",
            "Violet"
    );
    private Button carButton[];
    private Circle carStatus[];
    private Label carSpeedLabel[];
    private BorderPane centerPane;
    private Label menuLabel, cLabel;
    private SimulationModel simulationModel;
    private SimulationPane sp;
    private int secondCounter;
    private Thread hazardThread1, hazardThread2, speedThread;
    private boolean hazardFlag, overSpeed, hazardFlag2;
    private SimulationController sc;
    private int demoCars;
    private int brakeCount;

    /**
     * This method is used to initialize Simulation window
     * @throws java.io.IOException
     */
    @FXML
    public void initialize() throws IOException {
        if (Demo.demoSelected) {
            disableOptions();
            demoCars = (int) (2 + (new Random()).nextInt(4));
            brakeCount = demoCars;
        }
        initializeUI();
        addListeners();
        simulationModel = new SimulationModel();
        sc = this;
    }

    /**
     * This method is used to add listeners to menu bar and slider
     */
    private void addListeners() {
        menuLabel.setOnMouseClicked((event) -> {
            JOptionPane.showMessageDialog(null, "----------------------------------------------------------------------------\nSafe Driving Practice Simulator v2.0.\nAdvanced Java project.\nDeveloped by\nHarshad Shettigar.\nFaculty of Science,Engineering and Technology.\n----------------------------------------------------------------------------");
        });
        durationSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            durationLabel.setText(newValue.intValue() + " seconds");
        });
    }

    /**
     * This method is used to exit simulation
     */
    @FXML
    private void exitSimulation() {
        System.exit(0);
    }

    /**
     * This method is used to start simulation
     */
    @FXML
    private void startSimulation() {
        setTimer();
        startTimer();
        mainBorderPane.getChildren().remove(0);
        centerBox.getChildren().remove(0);
        if (!Demo.demoSelected) {
            sp = new SimulationPane(carNumbers.getSelectionModel().getSelectedItem(), carColors.getSelectionModel().getSelectedIndex(), schoolHours.isSelected(), roadRepair.isSelected());
        } else {
            sp = new SimulationPane(demoCars + "", carColors.getSelectionModel().getSelectedIndex(), true, false);
        }
        centerBox.getChildren().add(sp);
        disableOptions();
        addBrakeButtons();
        startRandomHazard();
        startRandomSpeedIncrease();
    }

    /**
     * This method is used to initialize UI
     */
    private void initializeUI() {
        menuLabel = new Label("About");
        Menu about = new Menu();
        about.setGraphic(menuLabel);
        menuBar.getMenus().add(about);
        carNumbers.setItems(carNoOptions);
        carNumbers.getSelectionModel().select(0);
        carColors.setItems(carColOptions);
        carColors.getSelectionModel().select(4);
    }

    /**
     * This method is used to disable options for 'demo'
     */
    private void disableOptions() {
        durationSlider.setDisable(true);
        carNumbers.setDisable(true);
        carColors.setDisable(true);
        schoolHours.setDisable(true);
        roadRepair.setDisable(true);
    }

    /**
     * This method is used to add buttons for each car to brake
     */
    private void addBrakeButtons() {
        if (Demo.demoSelected) {
            carButton = new Button[demoCars];
        } else {
            carButton = new Button[Integer.parseInt((String) carNumbers.getSelectionModel().getSelectedItem())];
        }
        simulationModel.initializeCarCount(carButton.length);
        carStatus = new Circle[carButton.length];
        carSpeedLabel = new Label[carButton.length];
        for (int i = 0; i < carButton.length; i++) {
            final int count = i;
            carButton[i] = new Button("Car " + (i + 1) + " brake");
            carSpeedLabel[i] = new Label("");
            carStatus[i] = new Circle(5, Color.BLACK);
            carButton[i].setUserData(i);
            carButton[i].setOnAction((ActionEvent event) -> {
                Button obj = (Button) event.getSource();
                sp.slowCar(Integer.parseInt(obj.getUserData().toString()));
                if (hazardFlag && (sp.getPathTransitionTime().toSeconds() < 3.0)) {
                    if (changeBrakeStatus(count)) {
                        try {
                            simulationModel.increaseCarBrake(count);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (hazardFlag2 && (sp.getFadeTransitionTime().toSeconds() < 3.0)) {
                    if (changeBrakeStatus(count)) {
                        try {
                            simulationModel.increaseCarBrake(count);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (overSpeed) {
                    sp.decreaseSpeed();
                    sc.updateSpeed();
                }
            });
            buttonGrid.add(carButton[i], 0, i);
            buttonGrid.add(carStatus[i], 1, i);
            buttonGrid.add(carSpeedLabel[i], 2, i);
        }
    }

    /**
     * This method is used to change status for each car
     * @param index car number
     */
    private boolean changeBrakeStatus(int index) {
        Color c = Color.LIMEGREEN;
        if ((carStatus[index].getFill()).equals(c)) {
            return false;
        } else {
            carStatus[index].setFill(c);
            return true;
        }
    }

    /**
     * This method is used to reset status for each car
     */
    private void resetBrakeStatus() {
        for (int i = 0; i < carStatus.length; i++) {
            if (((carStatus[i].getFill()).equals(Color.BLACK))) {
                simulationModel.increaseMissedBrake(i);
            }
            carStatus[i].setFill(Color.BLACK);
        }
    }

    /**
     * This method is used to initialize timer
     */
    private void setTimer() {
        if (Demo.demoSelected) {
            secondCounter = 120;
        } else {
            secondCounter = (int) durationSlider.getValue();
        }
        timeLeft.setText(((int) durationSlider.getValue()) + "");
    }

    /**
     * This method is used to start timer
     */
    private void startTimer() {
        secondCounter=60;
        Task t = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        secondCounter--;
                        changeLabel();
                        if (secondCounter == 0) {
                            break;
                        }
                    }
                    stopAll();
                    addBarChart();
                    if (Demo.demoSelected) {
                        Platform.runLater(sc::loadChooser);
                    }
                    sc.sendBarChartDetails();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(t).start();
    }

    /**
     * This method is used to change timer label
     */
    private void changeLabel() {
        Platform.runLater(() -> {
            timeLeft.setText(secondCounter + "");
        });
    }

    /**
     * This method is used to start thread for Hazards
     */
    private void startRandomHazard() {
        Task t = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    Thread.sleep(getSleepSeconds(4001));
                    Platform.runLater(sp::showHazard1);
                    simulationModel.increaseTotalHazardCount();
                    hazardFlag = true;
                    if (Demo.demoSelected) {
                        Thread.sleep(2000);
                        Platform.runLater(sc::autoBrake);
                    }
                    Thread.sleep(8000);
                    Platform.runLater(sp::removeHazard1);
                    resetBrakeStatus();
                    hazardFlag = false;
                }
            }
        };
        hazardThread1 = new Thread(t);
        hazardThread1.start();
        if (roadRepair.isSelected()) {
            Task t1 = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        Thread.sleep(getSleepSeconds(45001));
                        Platform.runLater(sp::showHazard2);
                        simulationModel.increaseTotalHazardCount();
                        hazardFlag2 = true;
                        Thread.sleep(8000);
                        Platform.runLater(sp::removeHazard2);
                        resetBrakeStatus();
                        hazardFlag2 = false;
                    }
                }
            };
            hazardThread2 = new Thread(t1);
            hazardThread2.start();
        }
    }

    /**
     * This method is used to automatically apply brake in 'demo' mode
     */
    public void autoBrake() {
        for (int i = 0; i < brakeCount; i++) {
            carButton[i].fire();
        }
        brakeCount--;
        if (brakeCount == 0) {
            brakeCount = demoCars;
        }
    }

    /**
     * This method is used to sleep threads running Hazard
     */
    private long getSleepSeconds(int range) {
        return (long) (Math.random() * range) + 15000;
    }

    /**
     * This method is used to randomly increase speed of background (basically increasing car speed)
     */
    private void startRandomSpeedIncrease() {
        Task t = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(sc::updateSpeed);
                while (true) {
                    Thread.sleep(1000);
                    Platform.runLater(sp::increaseSpeed);
                    Platform.runLater(sc::updateSpeed);
                }
            }
        };
        speedThread = new Thread(t);
        speedThread.start();
    }

    /**
     * This method is used to update label which displays car speed
     */
    public void updateSpeed() {
        boolean change = false;
        for (int i = 0; i < carSpeedLabel.length; i++) {
            carSpeedLabel[i].setText(((int) (sp.getTransition().getRate() * 25)) + "km/hr");
            if ((int) (sp.getTransition().getRate() * 25) > 40) {
                changeSpeedLabelColor(i, Color.RED);
                change = true;
            } else {
                changeSpeedLabelColor(i, Color.BLACK);
            }
        }
        if (change) {
            overSpeed = true;
            sp.changeCarColor();
        }
    }

    /**
     * This method is used to change color of speed label when over-speeding
     * @param index car number label
     * @param color color for label text
     */
    public void changeSpeedLabelColor(int index, Color color) {
        carSpeedLabel[index].setTextFill(color);
    }

    /**
     * This method is used to stop all the threads
     */
    public void stopAll() {
        Platform.runLater(sp::stopSimulation);
        hazardThread1.interrupt();
        if ((!(Demo.demoSelected))&&((roadRepair.isSelected()))) {
            hazardThread2.interrupt();
        }
        speedThread.interrupt();
        for (Button carButton1 : carButton) {
            carButton1.setDisable(true);
        }
    }

    /**
     * This method is used to send bar chart details to view pane
     */
    public void sendBarChartDetails() {
        int totalHazardCount = simulationModel.getTotalHazard();
        int braked[] = simulationModel.getTotalBrakeCount();
        int missed[] = new int[carButton.length];
        for (int i = 0; i < missed.length; i++) {
            missed[i] = totalHazardCount - braked[i];
        }
        sp.barChartDetails(braked, missed, simulationModel.getTotalHazard());
        sp.brakeMapDetails(simulationModel.getBrakeApplied(), simulationModel.getBrakeMissed());
    }

    /**
     * This method is used to display bar chart and comparison table
     */
    public void addBarChart() {
        Platform.runLater(sp::showBarChart);
        Platform.runLater(sp::showCarTable);
        if (!Demo.demoSelected) {
            Platform.runLater(sp::showSaveFileDialog);
        }
    }

    /**
     * This method is used to display dialog to choose 'demo' or 'practice'
     */
    public void loadChooser() {
        Alert demoAlert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        demoAlert.setTitle("Demo");
        demoAlert.setHeaderText("Do you want to run the automated demo again?");
        Alert practiceAlert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        practiceAlert.setTitle("Practice");
        practiceAlert.setHeaderText("Do you want to proceed to practice test?");
        if (Demo.demo < 3) {
            demoAlert.showAndWait();
        }
        if (demoAlert.getResult() == ButtonType.YES && (Demo.demo < 3)) {
            try {
                Stage primaryStage;
                primaryStage = new Stage();
                Demo.demo++;
                System.err.println(Demo.demo);
                Demo.demoSelected = true;
                primaryStage.setTitle("Safe-Driving Simulator (Automated Demo)");
                Parent root = FXMLLoader.load(getClass().getResource("/view/SimulationView.fxml"));
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                Platform.runLater(primaryStage::show);
                Stage stage = ((Stage) menuBar.getScene().getWindow());
                stage.close();
            } catch (IOException ex) {
            }
        } else {
            if (Demo.demoSelected) {
                Demo.demoSelected = false;
            }
            practiceAlert.showAndWait();
        }
        if (practiceAlert.getResult() == ButtonType.YES) {
            try {
                Stage primaryStage;
                primaryStage = new Stage();
                Demo.demoSelected = false;
                primaryStage.setTitle("Safe-Driving Simulator");
                Parent root = FXMLLoader.load(getClass().getResource("/view/SimulationView.fxml"));
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                Platform.runLater(primaryStage::show);
                Stage stage = ((Stage) menuBar.getScene().getWindow());
                stage.close();
            } catch (IOException ex) {
            }
        }
    }
}
