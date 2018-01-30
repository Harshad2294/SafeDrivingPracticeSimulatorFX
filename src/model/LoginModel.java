package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This is a login model to read login details from file and validate entered details
 * @author Harshad Shettigar
 * @version 1.0
 */
public class LoginModel {

    //class variables
    private BufferedReader br;
    private HashMap<String, String> loginDetails;
    private String temp="";
    private StringTokenizer st;
    private File file = new File("files/Login.txt");

    /**
     * constructor for login model class
     */
    public LoginModel() {
        try {
            loginDetails = new HashMap<String, String>();
            br = new BufferedReader(new FileReader(file));
            while ((temp = br.readLine()) != null) {
                st = new StringTokenizer(temp, ",");
                String username = st.nextToken();
                String password = st.nextToken();
                loginDetails.put(username, password);
            }
            br.close();
        } catch (IOException ex) {}
    }

    /**
     * Method to validate entered detaills
     * @param username entered username
     * @param password entered password
     * @return returns username
     */
    public String validate(String username, String password) {
        if (loginDetails.containsKey(username)) {
            if ((loginDetails.get(username)).equals(password)) {
                return loginDetails.get(username);
            }
        }
        return null;
    }
}