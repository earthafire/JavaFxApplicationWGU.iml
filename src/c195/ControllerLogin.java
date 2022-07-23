package c195;

import c195.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.DBQuery;
import utils.FXMLDialogs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Login page logic
 * @author Addison Ashworth
 */
public class ControllerLogin {
    private final static String LOGIN_ACTIVITY_FILENAME = "/login_activity.txt";
    private String language = "en";

    @FXML
    private Button login;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Label header;

    @FXML
    private Label systemtime;


    /**
     * checks the user input for errors when "login" button is pressed
     * on veritable input, checks with database for matches
     *
     * @param event Provided by JavaFX
     */
    @FXML
    public void checkLogin(ActionEvent event){
        String usernameTxt = username.getText();
        String passwordTxt = password.getText();

        if(usernameTxt.length() < 1){
        //no username input
            switch (language) {
                case "fr":
                    FXMLDialogs.error("nom d'utilisateur non valide");
                    break;
                default:
                    FXMLDialogs.error("invalid username");
                    break;
            }

        } else if(passwordTxt.length() < 1){
        //no password input
            switch (language) {
                case "fr":
                    FXMLDialogs.error("mot de passe non valide");
                    break;
                default:
                    FXMLDialogs.error("invalid password");
                    break;
            }

        } else {
        //valid username and password
            try {
                logAttempt(usernameTxt, passwordTxt);
            } catch (IOException e) {
                System.out.println("Failed to log attempt");
                e.printStackTrace();
            }

            try{
                //query db for User object corresponding to username and password
                User user = DBQuery.getUser(usernameTxt, passwordTxt);
                if(user != null){
                    // if there is a user with that password, change scenes
                    ControllerMain controller = FXMLDialogs.changeMainScene("../fxml/main.fxml").<ControllerMain>getController();;
                    controller.setLanguage(language);
                    controller.setUser(user);
                } else {
                    // if there is no user with that password, giver user an error
                    switch (language) {
                        case "fr":
                            FXMLDialogs.error("aucun utilisateur avec ce mot de passe");
                            break;
                        default:
                            FXMLDialogs.error("no user with that password");
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Appends a login attempt to the default login file
     *
     * @throws IOException IOException if can't access db connection
     * @param usernameTxt username text of attempt
     * @param passwordTxt password text of attempt
     */
    public void logAttempt(String usernameTxt, String passwordTxt) throws IOException {
        File file = new File(LOGIN_ACTIVITY_FILENAME);
        if(!file.exists()){
            file.mkdir();
        }

        FileWriter fw = new FileWriter(file.getName(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        LocalDateTime now = LocalDateTime.now();
        bw.write(now.toLocalDate().toString() + " " + now.toLocalTime().toString());
        bw.write(" USERNAME: " + usernameTxt + " PASSWORD: " + passwordTxt);
        bw.newLine();
        bw.close();
    }

    /**
     * takes system date time and "displays it in a label on the log-in form"
     * part of requirement A1.
     *
     * @param zdt system zoned date time taken at application start
     */
    public void setTimeZone(ZonedDateTime zdt) {
        systemtime.setText(zdt.getZone().toString());
    }

    /**
     * changes form language if new language is set
     *
     * @param language form language to change to
     */
    public void setLanguage(String language){
        if(!this.language.equalsIgnoreCase(language)){
            this.language = language;
            switch (language){

                case "en":
                    header.setText("C195 Appointment Manager");
                    login.setText("Login");
                    username.setPromptText("Username");
                    password.setPromptText("Password");
                    break;

                case "fr":
                    header.setText("C195 Gestionnaire de rendez-vous");
                    login.setText("Connexion");
                    username.setPromptText("Nom d'utilisateur");
                    password.setPromptText("Mot de passe");
                    break;

                default:
                    System.out.println("ERROR: unknown language, language has not been changed");
            }
        }
    }
}
