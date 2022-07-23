package c195;

import c195.model.Country;
import c195.model.Customer;
import c195.model.Division;
import c195.model.User;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.DBQuery;
import utils.FXMLDialogs;

/**
 * Customer "edit" and "add" page logic
 * @author Addison Ashworth
 */
public class ControllerCustomer {
    private ControllerMain controllerMain;

    private FilteredList<Division> divisionFilteredList;

    @FXML
    private Label title;

    @FXML
    private Label Customer_ID;

    @FXML
    private TextField Customer_NameTextField;

    @FXML
    private TextField AddressTextField;

    @FXML
    private TextField Postal_CodeTextField;

    @FXML
    private TextField PhoneTextField;

    @FXML
    private ComboBox<Country> CountryComboBox;

    @FXML
    private ComboBox<Division> StateProvinceComboBox;

    @FXML
    private Button submitButton;


    /**
     * prepares the form for an edit, filling out all the fields from the old customer object
     *
     * @param controllerMain to pull data from last window
     * @param customer customer to edit
     */
    void editCustomer(ControllerMain controllerMain, Customer customer){
        title.setText("Edit");
        this.controllerMain = controllerMain;
        Customer_ID.setText(String.valueOf(customer.getCustomer_ID()));
        Customer_NameTextField.setText(customer.getCustomer_Name());
        AddressTextField.setText(customer.getAddress());
        Postal_CodeTextField.setText(customer.getPostal_Code());
        PhoneTextField.setText(customer.getPhone());
        setComboBoxes(controllerMain.getCountries(), controllerMain.getDivisions());

        for(Division division: controllerMain.getDivisions()) {
            if (division.getDivision_ID() == customer.getDivision_ID()) {
                for (Country country : controllerMain.getCountries()) {
                    if (country.getCountry_ID() == division.getCOUNTRY_ID()) {
                        CountryComboBox.getSelectionModel().select(country);
                        break;
                    }
                }
                StateProvinceComboBox.getSelectionModel().select(division);
                break;
            }
        }
    }

    /**
     * prepares the form by giving you a blank sheet to make a new customer on
     *
     * @param controllerMain previous form to pull data from
     */
    void addCustomer(ControllerMain controllerMain){
        title.setText("Add");
        this.controllerMain = controllerMain;
        setComboBoxes(controllerMain.getCountries(), controllerMain.getDivisions());
    }

    /**
     * loads the countries and divisions into their combo boxes and
     * tells the program how to make them human-readable
     *
     * @param countries list of possible countries
     * @param divisions list of possible divisions
     */
    void setComboBoxes(ObservableList<Country> countries, ObservableList<Division> divisions){
        CountryComboBox.setItems(countries);
        CountryComboBox.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country country) {
                return country.getCountry();
            }

            @Override
            public Country fromString(String s) {
                return null;
            }
        });

        divisionFilteredList = new FilteredList<>(divisions, b -> true);
        StateProvinceComboBox.setItems(divisionFilteredList);
        StateProvinceComboBox.setConverter(new StringConverter<Division>() {
            @Override
            public String toString(Division division) {
                return division.getDivision();
            }

            @Override
            public Division fromString(String s) {
                return null;
            }
        });
    }

    /**
     * filters divisions list based on country selection using lambda expression.
     * I chose to use a lambda expression here because its a very simple piece of logic
     * that is unique to this method.
     *
     * @param event provided by JavaFX
     */
    @FXML
    void onCountrySelect(ActionEvent event) {
        divisionFilteredList.setPredicate(division -> {
            if(division.getCOUNTRY_ID() == CountryComboBox.getSelectionModel().getSelectedItem().getCountry_ID()){
                return true;
            }

            return false;
        });
    }

    /**
     * Calls necessary interactions to convert View form to Model and database
     *
     * @param event provided by JavaFX
     */
    @FXML
    void submit(ActionEvent event) {
        if(isFormValid()){
            //if there is a previous customer id, get it
            int custID;
            if(Customer_ID.getText().length() < 1){
                custID = 0;
            } else {
                custID = Integer.parseInt(Customer_ID.getText());
            }

            Customer customer = new Customer(
                    custID,
                    Customer_NameTextField.getText(),
                    AddressTextField.getText(),
                    Postal_CodeTextField.getText(),
                    PhoneTextField.getText(),
                    StateProvinceComboBox.getSelectionModel().getSelectedItem().getDivision_ID());

            if(title.getText().equals("Add")){
                String statement = customer.getInsertString(controllerMain.getUser().getUser_Name());
                DBQuery.executeStatement(statement);
            } else if (title.getText().equals("Edit")){
                String statement = customer.getUpdateString(controllerMain.getUser().getUser_Name());
                DBQuery.executeStatement(statement);

            } else {
                System.out.println("I shouldn't ever print!");
            }
            controllerMain.updateCustomers();

            Stage stage = (Stage) title.getScene().getWindow();
            stage.close();
        }
    }

    public boolean isFormValid(){
        if(Customer_NameTextField.getText().length() < 1){
            fieldNotValidError("Customer_Name");
            return false;
        } else if (AddressTextField.getText().length() < 1){
            fieldNotValidError("Address");
            return false;
        } else if (Postal_CodeTextField.getText().length() < 1){
            fieldNotValidError("Postal_Code");
            return false;
        } else if (PhoneTextField.getText().length() < 1){
            fieldNotValidError("Phone");
            return false;
        } else if (CountryComboBox.getSelectionModel().isEmpty()){
            fieldNotValidError("Country");
            return false;
        } else if (StateProvinceComboBox.getSelectionModel().isEmpty()){
            fieldNotValidError("State/Province");
            return false;
        }
 
        return true;
    }

    /**
     * helper function for translating similar messages about fields being empty
     *
     * @param value invalid field name
     */
    public void fieldNotValidError(String value){
        switch (controllerMain.getLanguage()) {
            case "fr":
                FXMLDialogs.error(value + " non valide");
                break;
            default:
                FXMLDialogs.error("invalid " + value);
                break;
        }
    }
}
