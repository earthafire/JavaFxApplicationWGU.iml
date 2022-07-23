package c195;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;
import utils.FXMLDialogs;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.*;
import java.util.Locale;

/**
 * Software driver
 * @author Addison Ashworth
 */
public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(FXMLDialogs.class.getResource("../fxml/login.fxml"));
        try {
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            stage.close();
        }
        stage.setTitle("C195");
        primaryStage = stage;
        Main.primaryStage = primaryStage;

        ControllerLogin login = fxmlLoader.<ControllerLogin>getController();
        login.setTimeZone(ZonedDateTime.now());
        login.setLanguage(Locale.getDefault().getLanguage());

        primaryStage.show();
    }

    /**
     * application driver
     * @param args command line arguments
     */
    public static void main(String[] args){
        //init db connection
        Connection connection = DBConnection.startConnection();

        try{
            DBQuery.setStatement(connection);
        } catch (SQLException e){
            System.out.println(e);
        }

        //java fx scene
        launch(args);

        DBConnection.closeConnection();
    }

    /**
     * helper function for swapping scenes
     * @param scene scene to switch to
     */
    public static void setPrimaryScene(Scene scene){
        Main.primaryStage.setScene(scene);
    }
}
