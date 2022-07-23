package utils;

import c195.ControllerErrorDialog;
import c195.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * commonly used dialogs (errors, alerts, etc.)
 */
public class FXMLDialogs {
    /**
     * gives error message to user, error message is given as string
     *
     * @param error Message to show on error dialogue
     */
    public static void error(String error){
        FXMLLoader loader = openNewWindow("../fxml/dialogueerror.fxml", "ERROR");
        if(loader != null){
            ControllerErrorDialog controller = loader.<ControllerErrorDialog>getController();
            controller.setErrorMessage(error);
        }
    }

    /**
     * opens new message as modal, title is bold, message is message
     *
     * @param message message to show
     * @param title window title
     */
    public static void customMessage(String message, String title){
        FXMLLoader loader = openNewWindow("../fxml/dialogueerror.fxml", "ALERT");
        if(loader != null){
            ControllerErrorDialog controller = loader.<ControllerErrorDialog>getController();
            controller.setErrorMessage(message);
            controller.setBoldTitle(title);
        }
    }

    /***
     * opens given fxml file in new modal window with title given
     *
     * @param file filepath to .fxml that should be opened
     * @param title window title
     * @return FXMLLoader object created
     */
    public static FXMLLoader openNewWindow(String file, String title){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(FXMLDialogs.class.getResource(file));

        try {
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            stage.close();
            return null;
        }

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.show();

        return fxmlLoader;
    }

    /**
     * changes the main scene to scene from a string fxml file
     *
     * @param file path to file to load
     * @return FXMLLoader of loaded scene
     */
    public static FXMLLoader changeMainScene(String file){
        Scene scene;
        FXMLLoader fxmlLoader = new FXMLLoader(FXMLDialogs.class.getResource(file));

        try {
            Parent root = fxmlLoader.load();
            scene = new Scene(root);
        } catch (Exception e) {
            return null;
        }

        Main.setPrimaryScene(scene);
        return fxmlLoader;
    }
}
