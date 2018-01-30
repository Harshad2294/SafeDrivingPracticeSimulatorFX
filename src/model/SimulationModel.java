package model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This is a model class for Simulation to maintain details about count of car brakes and hazard
 * @author Harshad Shettigar
 * @version 1.0
 */
public class SimulationModel {

    //class variables
    private int totalHazardCount;
    private int carBrakeCount[];
    private HashMap<Integer, ArrayList<String>> brakeMap;
    private HashMap<Integer, ArrayList<String>> missedMap;

    /**
     * Constructor for model
     */
    public SimulationModel() {
        totalHazardCount = 0;
    }

    /**
     * Method to initialize hashmap with count of cars
     * @param numberOfCars count of cars
     */
    public void initializeCarCount(int numberOfCars) {
        carBrakeCount = new int[numberOfCars];
        brakeMap = new HashMap<Integer, ArrayList<String>>();
        missedMap = new HashMap<Integer, ArrayList<String>>();
    }

    /**
     * Method to increase car brake of particular car number
     * @param carNumber car number
     */
    public void increaseCarBrake(int carNumber) {
        carBrakeCount[carNumber]++;
        ArrayList<String> a = new ArrayList<>();
        if (brakeMap.containsKey(carNumber)) {
            a.addAll(brakeMap.get(carNumber));
            a.add(LocalDate.now() + " " + LocalTime.now());
            brakeMap.replace(carNumber, a);
        } else {
            a.add(LocalDate.now() + " " + LocalTime.now());
            brakeMap.put(carNumber, a);
        }
    }

    /**
     * Method to increase missed brake of particular car number
     * @param carNumber car number
     */
    public void increaseMissedBrake(int carNumber) {
        ArrayList<String> a = new ArrayList<>();
        if (missedMap.containsKey(carNumber)) {

            a.addAll(missedMap.get(carNumber));
            a.add(LocalDate.now() + " " + LocalTime.now());
            missedMap.replace(carNumber, a);
        } else {
            a.add(LocalDate.now() + " " + LocalTime.now());
            missedMap.put(carNumber, a);
        }
    }

    /**
     * Method to increase hazard count
     */
    public void increaseTotalHazardCount() {
        totalHazardCount++;
    }

    /**
     * Method to get count of hazard
     * @return count of hazards
     */
    public int getTotalHazard() {
        return totalHazardCount;
    }

    /**
     * Method to get count of brakes applied of all cars
     * @return array list of brakes applied of all cars
     */
    public int[] getTotalBrakeCount() {
        return carBrakeCount;
    }

    /**
     * Method to get details of cars where car did not apply brake while hazard
     * @return details of cars where car did not apply brake while hazard
     */
    public HashMap getBrakeMissed() {
        return missedMap;
    }

    /**
     * Method to get details of cars where car applied brake while hazard
     * @return details of cars where car applied brake while hazard
     */
    public HashMap getBrakeApplied() {
        return brakeMap;
    }

    /**
     * Method to store XML to a file
     * @param missedData details of car of missed brakes of car in need of control
     * @param brakedData details of car of brakes applied car in need of control
     * @param carNumber car number in need of control
     */
    public void storeToXML(ArrayList<String> missedData, ArrayList<String> brakedData, int carNumber) {
        try {
            if (!(new File("data.xml")).exists()) {
                createNewXML();
            }
            Document doc = (DocumentBuilderFactory.newInstance()).newDocumentBuilder().parse(new File("data.xml"));
            Element root = doc.getDocumentElement();
            int count = doc.getElementsByTagName("SimulationResult").getLength();
            Element simResult = doc.createElement("SimulationResult");
            Attr dataNumber = doc.createAttribute("SerialNumber");
            if (count == 0) {
                dataNumber.appendChild(doc.createTextNode("1"));
            } else {
                dataNumber.appendChild(doc.createTextNode((count + 1) + ""));
            }
            simResult.setAttributeNode(dataNumber);
            Element carNumberTag = doc.createElement("CarNumber");
            carNumberTag.appendChild(doc.createTextNode(carNumber + ""));
            simResult.appendChild(carNumberTag);

            Element missedDataTag = doc.createElement("MissedBrakeDateTime");
            for (int i = 0; i < missedData.size(); i++) {
                Element tag = doc.createElement("Data");
                tag.appendChild(doc.createTextNode(missedData.get(i)));
                missedDataTag.appendChild(tag);
            }
            simResult.appendChild(missedDataTag);

            Element brakedDataTag = doc.createElement("AppliedBrakeDateTime");
            for (int i = 0; i < brakedData.size(); i++) {
                Element tag = doc.createElement("Data");
                tag.appendChild(doc.createTextNode(brakedData.get(i)));
                brakedDataTag.appendChild(tag);
            }
            simResult.appendChild(brakedDataTag);

            Element totalMissed = doc.createElement("TotalBrakesMissed");
            totalMissed.appendChild(doc.createTextNode(missedData.size() + ""));
            simResult.appendChild(totalMissed);
            Element totalApplied = doc.createElement("TotalBrakesApplied");
            totalApplied.appendChild(doc.createTextNode(brakedData.size() + ""));
            simResult.appendChild(totalApplied);
            Element totalHazards = doc.createElement("TotalHazardsEncountered");
            totalHazards.appendChild(doc.createTextNode((brakedData.size() + missedData.size()) + ""));
            simResult.appendChild(totalHazards);

            root.appendChild(simResult);
            DOMSource domSource = new DOMSource(doc);
            Transformer xmlTrans = TransformerFactory.newInstance().newTransformer();
            xmlTrans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            xmlTrans.setOutputProperty(OutputKeys.INDENT, "yes");
            xmlTrans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xmlTrans.transform(domSource, new StreamResult("data.xml"));
        } catch (IOException | IllegalArgumentException | ParserConfigurationException | TransformerException | DOMException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to create a new XML if it does not exist
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    private void createNewXML() throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        Document doc = (DocumentBuilderFactory.newInstance()).newDocumentBuilder().newDocument();
        Element root = doc.createElement("SimulationResults");
        doc.appendChild(root);
        DOMSource domSource = new DOMSource(doc);
        Transformer xmlTrans = TransformerFactory.newInstance().newTransformer();
        xmlTrans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        xmlTrans.setOutputProperty(OutputKeys.INDENT, "yes");
        xmlTrans.transform(domSource, new StreamResult("data.xml"));
    }
}
