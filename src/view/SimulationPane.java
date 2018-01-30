package view;

import model.Demo;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import model.SimulationModel;

/**
 * This is a class to display cars, road and hazard, basically it is a part of View component of entire MVC
 * @author Harshad Shettigar
 * @version 1.0
 */
public class SimulationPane extends Pane {

    //class variables
    private final double CAR_SPACING = 30;
    private final double CAR_HEIGHT = 40;
    private final String IMAGE_PATH = "files\\images\\car";
    private final String IMAGE_PATH_1 = "files\\images\\";
    private final int BACKGROUND_HEIGHT = 700;
    private final double CAR_WIDTH = 30;
    private final double HUES[] = {0.1, 0.4, 0.6, 1, -0.4, -0.18}; // Orange, Olive Green, Green, Cyan, Violet, Pink
    private final double selectedHue;
    private final int numberOfCars;
    private final boolean schoolHours;
    private final boolean roadRepair;
    private double carSpacingCount = CAR_SPACING;
    private ImageView road, cars[];
    private AnchorPane vehicleBox;
    private TranslateTransition translateTransition;
    private PathTransition hazardPT;
    private FadeTransition roadWorkFT;
    private ImageView schoolZone, roadRepairZone, child, legend;
    private int hazardIndex = 0, maxMissed;
    private double defaultTransitionRate;
    private int carBrakesApplied[], carBrakesMissed[], totalHazards;
    private HashMap<Integer, ArrayList<String>> brakeMap, missedMap;
    private VBox vb;
    ArrayList<String> missedData, brakedData;
    private int maxIndex;

    /**
     * Constructor to initialize simulation details
     * @param numberOfCars number of cars to display
     * @param carColorIndex color of cars
     * @param schoolHours hazard type
     * @param roadRepair hazard type
     */
    public SimulationPane(Object numberOfCars, int carColorIndex, boolean schoolHours, boolean roadRepair) {
        this.numberOfCars = Integer.parseInt((String) numberOfCars);
        this.cars = new ImageView[this.numberOfCars];
        this.selectedHue = HUES[carColorIndex];
        this.schoolHours = schoolHours;
        this.roadRepair = roadRepair;
        createRoad();   //1
        createCars();
        if (Demo.demoSelected) {
            addLegend();
        }
        addCars();  //2
        addSchoolZone();
        addRoadRepairZone();
        this.getChildren().addAll(road, vehicleBox);
        if (schoolHours) {
            this.getChildren().add(schoolZone);
            hazardIndex++;  //3
        }
        this.setPrefSize(766, BACKGROUND_HEIGHT);
    }

    /**
     * Method to create road to display
     */
    private void createRoad() {
        try {
            road = new ImageView(new Image(new FileInputStream("files\\images\\roadtexture.png")));
            road.setFitWidth(400);
            runTransition();
            hazardIndex++;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimulationPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to create cars to display
     */
    private void createCars() {
        try {
            for (int i = 0; i < cars.length; i++) {
                cars[i] = new ImageView(new Image(new FileInputStream(IMAGE_PATH + (i + 1) + ".png")));
                cars[i].setY(carSpacingCount);
                carSpacingCount += CAR_HEIGHT + CAR_SPACING;
                cars[i].setX(20);
                cars[i].setFitHeight(CAR_HEIGHT);
                cars[i].setFitWidth(CAR_WIDTH);
                cars[i].setEffect(new ColorAdjust(selectedHue, 0, 0, 0));
            }
        } catch (Exception e) {
        }
    }

    /**
     * Method to add cars to VBox
     */
    private void addCars() {
        vehicleBox = new AnchorPane();
        vehicleBox.getChildren().addAll(cars);
        vehicleBox.setTranslateX(300);
        vehicleBox.setTranslateY(50);
        hazardIndex++;
    }

    /**
     * Method to display hazard hint (school zone)
     */
    private void addSchoolZone() {
        try {
            schoolZone = new ImageView(new Image(new FileInputStream(IMAGE_PATH_1 + "schoolzone.png")));
            schoolZone.setFitHeight(CAR_HEIGHT + 20);
            schoolZone.setFitWidth(CAR_WIDTH + 20);
            schoolZone.setX(120);
            schoolZone.setY(0);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimulationPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to display road work sign as a hazard
     */
    private void addRoadRepairZone() {
        try {
            roadRepairZone = new ImageView(new Image(new FileInputStream(IMAGE_PATH_1 + "roadwork.png")));
            roadRepairZone.setFitHeight(CAR_HEIGHT + 40);
            roadRepairZone.setFitWidth(CAR_WIDTH + 40);
            roadRepairZone.setX(390);
            roadRepairZone.setY(0);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimulationPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to move background road
     */
    private void runTransition() {
        Platform.runLater(() -> {
            translateTransition = new TranslateTransition(Duration.millis(5000), road);
            translateTransition.setFromY(-1200);
            translateTransition.setFromX(170);
            translateTransition.setToY(-1 * BACKGROUND_HEIGHT);
            translateTransition.setInterpolator(Interpolator.LINEAR);
            translateTransition.setCycleCount(Animation.INDEFINITE);
            translateTransition.play();
            defaultTransitionRate = translateTransition.getRate();
        });
    }

    /**
     * Method to get TranslateTrantition object
     * @return road transition 
     */
    public TranslateTransition getTransition() {
        return translateTransition;
    }

    /**
     * Method to slow down car when corresponding button pressed
     * @param index car number
     */
    public void slowCar(int index) {
        Platform.runLater(() -> {
            Path p = new Path();
            MoveTo mt = new MoveTo(cars[index].getX() + 15, cars[index].getY());
            LineTo lt1 = new LineTo(cars[index].getX() + 15, cars[index].getY() + 20);
            LineTo lt2 = new LineTo(cars[index].getX() + 15, cars[index].getY());
            p.getElements().add(mt);
            p.getElements().add(lt1);
            p.getElements().add(lt2);
            p.setTranslateY(20);
            PathTransition pt = new PathTransition();
            pt.setPath(p);
            pt.setDuration(Duration.millis(2000));
            pt.setNode(cars[index]);
            pt.setAutoReverse(true);
            pt.setCycleCount(1);
            pt.play();
        });
    }

    /**
     * Method to display hazard of child crossing road
     */
    public void showHazard1() {
        Platform.runLater(() -> {
            try {
                child = new ImageView(new Image(new FileInputStream(IMAGE_PATH_1 + "child.png")));
                child.setFitHeight(CAR_WIDTH);
                child.setFitWidth(CAR_HEIGHT);
                child.setX(345);
                child.setY(15);
                this.getChildren().add(child);  //4
                hazardPT = new PathTransition();
                hazardPT.setPath(new Line(180, 10, 600, 100));
                hazardPT.setDuration(Duration.millis(7000));
                hazardPT.setNode(child);
                hazardPT.setCycleCount(1);
                hazardPT.play();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SimulationPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * Method to display hazard of road works
     */
    public void showHazard2() {
        Platform.runLater(() -> {
            this.getChildren().add(roadRepairZone);  //5
            roadWorkFT = new FadeTransition();
            roadWorkFT.setDuration(Duration.millis(7000));
            roadWorkFT.setNode(roadRepairZone);
            roadWorkFT.setFromValue(1.0);
            roadWorkFT.setToValue(0.0);
            roadWorkFT.setCycleCount(1);
            roadWorkFT.play();
        });
    }

    /**
     * Method to decrease speed of background
     */
    public void decreaseSpeed() {
        translateTransition.setRate(defaultTransitionRate);
        for (int i = 0; i < cars.length; i++) {
            cars[i].setEffect(new ColorAdjust(selectedHue, 0, 0, 0));
        }
    }

    /**
     * Method to increase speed of background
     */
    public void increaseSpeed() {
        translateTransition.setRate(translateTransition.getRate() + 0.01);
    }

    /**
     * Method to change color of car, when over-speeding
     */
    public void changeCarColor() {
        for (int i = 0; i < cars.length; i++) {
            cars[i].setEffect(new ColorAdjust(HUES[5], 0, 0, 0));
        }
    }

    /**
     * Method to remove first hazard
     */
    public void removeHazard1() {
        this.getChildren().remove(hazardIndex);
    }

    /**
     * Method to remove second hazard
     */
    public void removeHazard2() {
        this.getChildren().remove(hazardIndex);
    }

    /**
     * Method to stop simulation
     */
    public void stopSimulation() {
        translateTransition.stop();
        hazardPT.stop();
        if (Demo.demoSelected) {
            this.getChildren().removeAll(road, vehicleBox, child, roadRepairZone, schoolZone, legend);
        }
        this.getChildren().removeAll(road, vehicleBox, child, roadRepairZone, schoolZone);
    }

    /**
     * Method to get time of first hazard
     * @return time elapsed
     */
    public Duration getPathTransitionTime() {
        return hazardPT.getCurrentTime();
    }

    /**
     * Method to get time of second hazard
     * @return time elapsed
     */
    public Duration getFadeTransitionTime() {
        return roadWorkFT.getCurrentTime();
    }

    /**
     * Method to receive bar chart details
     * @param carBrakesApplied number of brakes applied by all cars
     * @param carBrakesMissed number of brakes missed by all cars
     * @param totalHazards total hazards encountered by all cars
     */
    public void barChartDetails(int carBrakesApplied[], int carBrakesMissed[], int totalHazards) {
        this.carBrakesApplied = carBrakesApplied.clone();
        this.carBrakesMissed = carBrakesMissed.clone();
        this.totalHazards = totalHazards;
    }

    /**
     * Method to receive date and time of all cars when brakes applied or missed
     * @param brakeMap date, time details of cars when brake applied on hazard
     * @param missedMap date, time details of cars when brake missed on hazard 
     */
    public void brakeMapDetails(HashMap<Integer, ArrayList<String>> brakeMap, HashMap<Integer, ArrayList<String>> missedMap) {
        this.brakeMap = (HashMap<Integer, ArrayList<String>>) brakeMap.clone();
        this.missedMap = (HashMap<Integer, ArrayList<String>>) missedMap.clone();
    }

    /**
     * Method to display Bar chart
     */
    public void showBarChart() {
        final CategoryAxis cars = new CategoryAxis();
        final NumberAxis hazards = new NumberAxis();
        final BarChart<String, Number> bc = new BarChart<String, Number>(cars, hazards);
        bc.setTitle("Car statistics based on Hazard");
        cars.setLabel("Cars");
        hazards.setLabel("Number of hazards");
        XYChart.Series clicked = new XYChart.Series();
        clicked.setName("Brake apply successful");
        XYChart.Series missed = new XYChart.Series();
        missed.setName("Brake apply unsuccessful");
        for (int i = 0; i < carBrakesMissed.length; i++) {
            clicked.getData().add(new XYChart.Data("Car " + (i + 1), carBrakesApplied[i]));
        }
        for (int i = 0; i < carBrakesMissed.length; i++) {
            missed.getData().add(new XYChart.Data("Car " + (i + 1), carBrakesMissed[i]));
        }
        bc.getData().addAll(clicked, missed);
        bc.setTranslateX(100);
        bc.setTranslateY(5);
        this.getChildren().add(bc);
    }

    /**
     * Method to display comparison table of car in need of contro;
     */
    public void showCarTable() {
        try {
            maxIndex = 0;
            for (int i = 0; i < carBrakesMissed.length; i++) {
                if (carBrakesMissed[maxIndex] < carBrakesMissed[i]) {
                    maxIndex = i;
                }
            }
            missedData = new ArrayList<>();
            missedData.addAll(missedMap.get(maxIndex));
            brakedData = new ArrayList<>();
            brakedData.addAll(brakeMap.get(maxIndex));
            GridPane gp = new GridPane();
            gp.getColumnConstraints().add(new ColumnConstraints(170));
            gp.getColumnConstraints().add(new ColumnConstraints(170));
            gp.getColumnConstraints().add(new ColumnConstraints(170));
            gp.setVgap(1);
            gp.setHgap(1);
            gp.setGridLinesVisible(true);
            gp.addRow(0, new Label("Missed Brakes"), new Label("Applied Brakes"), new Label("Stars"));
            gp.getRowConstraints().add(new RowConstraints(30));
            int maxLength;
            if (brakedData.size() < missedData.size()) {
                maxLength = missedData.size();
            } else {
                maxLength = brakedData.size();
            }
            for (int i = 0; i < maxLength; i++) {
                if (i >= missedData.size()) {
                    gp.add(new Label("----"), 0, i + 1);
                } else {
                    gp.add(new Label(missedData.get(i)), 0, i + 1);
                }

                if (i >= brakedData.size()) {
                    gp.add(new Label("----"), 1, i + 1);
                    gp.add(new Label("----"), 2, i + 1);
                } else {
                    ImageView iv = new ImageView(new Image(new FileInputStream(IMAGE_PATH_1 + "stars.jpg")));
                    iv.setFitHeight(10);
                    iv.setFitWidth(50);
                    gp.add(new Label(brakedData.get(i)), 1, i + 1);
                    gp.add(iv, 2, i + 1);
                }
                gp.getRowConstraints().add(new RowConstraints(30));
            }
            ScrollPane sp = new ScrollPane();
            sp.setPrefViewportHeight(180);
            sp.setContent(gp);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            sp.setVmax(180);
            sp.setFitToWidth(true);
            sp.setTranslateX(130);
            sp.setTranslateY(470);
            Label carLabel = new Label("Car Number " + (maxIndex + 1)+" is in need of control.");
            carLabel.setTranslateX(130);
            carLabel.setTranslateY(450);
            this.getChildren().add(carLabel);
            this.getChildren().add(sp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to display dialog to save details to XML
     */
    public void showSaveFileDialog() {
        Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        saveAlert.setTitle("Save to XML");
        saveAlert.setHeaderText("Do you want to save data to XML file?");
        saveAlert.showAndWait();
        if (saveAlert.getResult() == ButtonType.YES) {
            new SimulationModel().storeToXML(missedData, brakedData, maxIndex + 1);
        }
    }

    /**
     * Method to display simulation details in 'demo' mode
     */
    public void addLegend() {
        try {
            legend = new ImageView(new Image(new FileInputStream(IMAGE_PATH_1 + "legend.png")));
            legend.setFitHeight(170);
            legend.setFitWidth(170);
            legend.setTranslateX(580);
            legend.setTranslateY(120);
            this.getChildren().add(legend);
            hazardIndex++;
        } catch (FileNotFoundException ex) {
        }
    }
}