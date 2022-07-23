package c195;

import c195.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import utils.DBQuery;
import utils.FXMLDialogs;

import java.sql.SQLException;
import java.time.Month;
import java.time.ZonedDateTime;

/**
 * Main page dialog logic
 * @author Addison Ashworth
 */
public class ControllerMain {
    private String language = "en";
    private User user;
    private String tab = "none";

    private ObservableList<Country> countries;
    private ObservableList<Division> divisions;
    private ObservableList<Contact> contacts;
    private ObservableList<User> users;
    private ObservableList<Customer> customers;
    private ObservableList<Appointment> appointments;
    private FilteredList<Appointment> appointmentsFilteredList;
    private FilteredList<Appointment> scheduleFilteredList;

    /**
     * represents the toggle group of the week buttons
     */
    private ToggleGroup weekToggle;

    /**
     * represents the toggle group of the month buttons
     */
    private ToggleGroup monthToggle;

    private TableColumn scheduleStartColumn;

    @FXML
    private Button customersButton;

    @FXML
    private Button appointmentsButton;

    @FXML
    private AnchorPane customersTab;

    @FXML
    private AnchorPane appointmentsTab;

    @FXML
    private TabPane reportsTab;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableView<Appointment> scheduleTable;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private RadioButton allMonthRadio;

    @FXML
    private RadioButton allWeekRadio;

    @FXML
    private ComboBox<Contact> contactComboBox;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private ComboBox<Division> stateProvinceComboBox;

    private FilteredList<Division> stateProvinceFilteredList;

    @FXML
    private Label totalCustomerLabel;

    @FXML
    private TextField typeTextArea;

    @FXML
    private Label totalApptLabel;

    /**
     * pulls data from database and loads it into Model
     */
    public void initialize(){
        //populate from database
        updateCustomers();
        updateAppointments();
        checkNearAppointment();

        try {
            countries = DBQuery.getCountries();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            divisions = DBQuery.getDivisions();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            contacts = DBQuery.getContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contactComboBox.setItems(contacts);
        contactComboBox.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                return contact.getContact_Name();
            }

            @Override
            public Contact fromString(String s) {
                return null;
            }
        });

        try {
            users = DBQuery.getUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initCustomerTable();
        initAppointmentTable();
        initScheduleTable();
        initToggles();
        initReportTwoComboBox();
        initReportThreeComboBox();
    }

    /**
     * prepares combo box for report 2 month filtering
     */
    public void initReportTwoComboBox() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("ALL");

        for(Month month : Month.values()){
            items.add(month.toString());
        }

        monthComboBox.setItems(items);
    }

    /**
     * prepares combo boxes for report 3 customer count filtering
     */
    private void initReportThreeComboBox() {
        countryComboBox.setItems(countries);
        countryComboBox.getSelectionModel().select(0);
        countryComboBox.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country country) {
                return country.getCountry();
            }

            @Override
            public Country fromString(String s) {
                return null;
            }
        });

        stateProvinceFilteredList = new FilteredList<>(divisions, p -> true);
        filterStateBox();
        stateProvinceComboBox.setItems(stateProvinceFilteredList);
        stateProvinceComboBox.setConverter(new StringConverter<Division>() {
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
     * Initialize toggle groups for filtering
     */
    public void initToggles() {
        weekToggle = allWeekRadio.getToggleGroup();
        monthToggle = allMonthRadio.getToggleGroup();

        monthToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {
                filterAppointments();
            }
        });
        weekToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) {
                filterAppointments();
            }
        });
    }


    /**
     * Controller: loads User Model and puts it in the javafx View
     */
    public void initCustomerTable() {
        TableColumn Customer_ID = new TableColumn("Customer_ID");
        Customer_ID.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("Customer_ID"));

        TableColumn Customer_Name = new TableColumn("Customer_Name");
        Customer_Name.setCellValueFactory(new PropertyValueFactory<Customer, String>("Customer_Name"));

        TableColumn Address = new TableColumn("Address");
        Address.setCellValueFactory(new PropertyValueFactory<Customer, String>("Address"));

        TableColumn Postal_Code = new TableColumn("Postal_Code");
        Postal_Code.setCellValueFactory(new PropertyValueFactory<Customer, String>("Postal_Code"));

        TableColumn Phone = new TableColumn("Phone");
        Phone.setCellValueFactory(new PropertyValueFactory<Customer, String>("Phone"));

        customerTable.getColumns().addAll(Customer_ID, Customer_Name, Address, Postal_Code, Phone);
    }

    /**
     * Controller: loads Appointment Model and puts it in the javafx View
     * LAMBDA EXPRESSION: used here to improve readability and flexibility of the
     * conversion from ZonedDateTime to string in the appointments table view.
     * (ie. could put start date and time and only end time, or change format
     * of the time and date for better readability)
     */
    public void initAppointmentTable() {
        TableColumn Appointment_ID = new TableColumn("Appointment ID");
        Appointment_ID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("Appointment_ID"));

        TableColumn Title = new TableColumn("Title");
        Title.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Title"));

        TableColumn Description = new TableColumn("Description");
        Description.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Description"));

        TableColumn Location = new TableColumn("Location");
        Location.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Location"));

        TableColumn Contact = new TableColumn("Contact");
        Contact.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>) p -> {
            String name = "";
            for(Contact contact: contacts){
                if(p.getValue().getContact_ID() == contact.getContact_ID()){
                    name = contact.getContact_Name();
                }
            }
            return new SimpleStringProperty(name);
        });

        TableColumn Type = new TableColumn("Type");
        Type.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Type"));

        TableColumn Start = new TableColumn("Start");
        Start.setPrefWidth(100.0);
        Start.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>) p -> {
            String time = p.getValue().getStart().toLocalTime().toString();
            String date = p.getValue().getStart().toLocalDate().toString();
            return new SimpleStringProperty(
                    time + " " + date);
        });

        TableColumn End = new TableColumn("End");
        End.setPrefWidth(100.0);
        End.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>) p -> {
            String time = p.getValue().getEnd().toLocalTime().toString();
            String date = p.getValue().getEnd().toLocalDate().toString();
            return new SimpleStringProperty(
                     time + " " + date);
        });

        TableColumn Customer_ID = new TableColumn("Customer ID");
        Customer_ID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("Customer_ID"));

        appointmentTable.getColumns().addAll(Appointment_ID, Title, Description, Location, Contact, Type, Start, End, Customer_ID);
    }

    /**
     * Controller: loads Appointment Model and puts it in the javafx View as schedule
     */
    public void initScheduleTable() {
        TableColumn Appointment_ID = new TableColumn("Appointment ID");
        Appointment_ID.setPrefWidth(115.0);
        Appointment_ID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("Appointment_ID"));

        TableColumn Title = new TableColumn("Title");
        Title.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Title"));

        TableColumn Description = new TableColumn("Description");
        Description.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Description"));


        TableColumn Type = new TableColumn("Type");
        Type.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Type"));

        scheduleStartColumn = new TableColumn("Start");
        scheduleStartColumn.setPrefWidth(100.0);
        scheduleStartColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>) p -> {
            String time = p.getValue().getStart().toLocalTime().toString();
            String date = p.getValue().getStart().toLocalDate().toString();
            return new SimpleStringProperty(
                    time + " " + date);
        });

        TableColumn End = new TableColumn("End");
        End.setPrefWidth(100.0);
        End.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Appointment, String>, ObservableValue<String>>) p -> {
            String time = p.getValue().getEnd().toLocalTime().toString();
            String date = p.getValue().getEnd().toLocalDate().toString();
            return new SimpleStringProperty(
                    time + " " + date);
        });

        TableColumn Customer_ID = new TableColumn("Customer ID");
        Customer_ID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("Customer_ID"));

        scheduleTable.getColumns().addAll(Appointment_ID, Title, Description, Type, scheduleStartColumn, End, Customer_ID);
    }

    /**
     * sets the language for applicable labels/buttons/fields
     *
     * @param language default language
     */
    public void setLanguage(String language){
        if(!this.language.equalsIgnoreCase(language)){
            this.language = language;
            switch (language){

                case "en":
                    addButton.setText(("Add"));
                    editButton.setText(("Edit"));
                    deleteButton.setText(("Delete"));
                    customersButton.setText("Customers");
                    appointmentsButton.setText("Appointments");
                    break;

                case "fr":
                    addButton.setText(("Aouter"));
                    editButton.setText(("Éditer"));
                    deleteButton.setText(("Supprimer"));
                    customersButton.setText("Clientèle");
                    appointmentsButton.setText("Rendez-vous");
                    break;

                default:
                    System.out.println("ERROR: unknown language, language has not been changed");
            }
        }
    }

    /**
     * saves user that logged in
     *
     * @param user user object representing the person using this software
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * fires when the add button is pushed, determines what is trying to be added
     *
     * @param event passed from javaFX
     */
    @FXML
    void add(ActionEvent event) {
        if(tab.equalsIgnoreCase("customers")){
            FXMLLoader loader = FXMLDialogs.openNewWindow("../fxml/customer.fxml", "Add Customer");
            if(loader != null){
                ControllerCustomer controller = loader.<ControllerCustomer>getController();
                controller.addCustomer(this);
            }
        } else if (tab.equalsIgnoreCase("appointments")){
            FXMLLoader loader = FXMLDialogs.openNewWindow("../fxml/appointment.fxml", "Add Appointment");
            if(loader != null){
                ControllerAppointment controller = loader.<ControllerAppointment>getController();
                controller.addAppointment(this);
            }
        }
    }

    /**
     * fires when the delete button is pushed, determines what is trying to be added
     *
     * @param event passed from javaFX
     */
    @FXML
    void delete(ActionEvent event) {
        if(tab.equalsIgnoreCase("customers")){
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if(selectedCustomer != null){
                //remove all appointments related to customer
                int appointmentsDeleted = DBQuery.executeStatement(selectedCustomer.getDeleteAllAppointementString());
                if(appointmentsDeleted > 0){
                    switch (language) {
                        case "fr":
                            FXMLDialogs.customMessage(appointmentsDeleted + " rendez-vous ont été déltétés pour le client avec ID#: " + selectedCustomer.getCustomer_ID(), "Alerte:");
                            break;
                        default:
                            FXMLDialogs.customMessage(appointmentsDeleted + " appointments were deleted for customer with ID#: " + selectedCustomer.getCustomer_ID(), "Alert:");
                            break;
                    }
                }
                //remove customer
                DBQuery.executeStatement(selectedCustomer.getDeleteString());
                switch (language) {
                    case "fr":
                        FXMLDialogs.customMessage("Client avec ID: " + selectedCustomer.getCustomer_ID() + " a été supprimé.", "Alerte:");
                        break;
                    default:
                        FXMLDialogs.customMessage("Customer with ID: " + selectedCustomer.getCustomer_ID() + " was deleted.", "Alert:");
                        break;
                }
                updateCustomers();
                updateAppointments();
            } else {
                objectNotSelectedError();
            }
        } else if (tab.equalsIgnoreCase("appointments")){
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if(selectedAppointment != null){
                //remove appointment
                DBQuery.executeStatement(selectedAppointment.getDeleteString());
                switch (language) {
                    case "fr":
                        FXMLDialogs.customMessage("Appointment avec ID: " + selectedAppointment.getAppointment_ID() + " et type: '" + selectedAppointment.getType() + "' a été supprimé.", "Alerte:");
                        break;
                    default:
                        FXMLDialogs.customMessage("Appointment with ID: " + selectedAppointment.getAppointment_ID() + " and type: '" + selectedAppointment.getType() + "' was deleted.", "Alert:");
                        break;
                }
                updateAppointments();
            } else {
                objectNotSelectedError();
            }
        }
    }

    /**
     * fires when the edit button is pushed, determines what is trying to be added
     *
     * @param event passed from javaFX
     */
    @FXML
    void edit(ActionEvent event) {
        if(tab.equalsIgnoreCase("customers")){
            if(customerTable.getSelectionModel().getSelectedItem() != null){
                FXMLLoader loader = FXMLDialogs.openNewWindow("../fxml/customer.fxml", "Edit Customer");
                if(loader != null){
                    ControllerCustomer controller = loader.<ControllerCustomer>getController();
                    controller.editCustomer(this, customerTable.getSelectionModel().getSelectedItem());
                }
            } else {
                objectNotSelectedError();
            }
        } else if (tab.equalsIgnoreCase("appointments")){
            if(appointmentTable.getSelectionModel().getSelectedItem() != null){
                FXMLLoader loader = FXMLDialogs.openNewWindow("../fxml/appointment.fxml", "Edit Appointment");
                if(loader != null){
                    ControllerAppointment controller = loader.<ControllerAppointment>getController();
                    controller.editAppointment(this, appointmentTable.getSelectionModel().getSelectedItem());
                }
            } else {
                objectNotSelectedError();
            }
        }
    }

    /**
     * helper function to warn user when object isn't selected to edit
     */
    public void objectNotSelectedError(){
        switch (language) {
            case "fr":
                FXMLDialogs.error("Sélectionnez un objet à modifier");
                break;
            default:
                FXMLDialogs.error("Please select an object to edit");
                break;
        }
    }

    /**
     * shows all appointments in table view
     *
     * @param event provided by JAVAFX
     */
    @FXML
    void showAll(ActionEvent event) {
        appointmentsFilteredList.setPredicate(p -> true);
    }


    /**
     * filters appointments based on radio buttons
     *
     * LAMBDA EXPRESSION:
     * used here to create very readable filtering options for filtered list,
     * very easy to understand what each predicate is
     */
    private void filterAppointments() {
        String selectedWeekToggle =
                ((RadioButton) weekToggle
                        .getSelectedToggle())
                        .getText();
        String selectedMonthToggle =
                ((RadioButton) monthToggle
                        .getSelectedToggle())
                        .getText();

        if(selectedWeekToggle.equals("All")){
            if(selectedMonthToggle.equals("All")){
                appointmentsFilteredList.setPredicate(appointment -> true);
            } else {
                appointmentsFilteredList.setPredicate(appointment -> {
                    if (appointment.getStart().getMonth() == Month.valueOf(selectedMonthToggle.toUpperCase())) {
                        return true;
                    }
                    return false;
                });
            }
        } else {
            if(selectedMonthToggle.equals("All")){
                int week =Integer.parseInt(selectedWeekToggle);
                appointmentsFilteredList.setPredicate(appointment -> {
                    int day = appointment.getStart().getDayOfMonth();
                    if(day <= week * 7 && day > week * 7 - 7){
                        return true;
                    }
                    return false;
                });
            } else {
                int week =Integer.parseInt(selectedWeekToggle);
                appointmentsFilteredList.setPredicate(appointment -> {
                    int day = appointment.getStart().getDayOfMonth();
                    if(appointment.getStart().getMonth() == Month.valueOf(selectedMonthToggle.toUpperCase()) && day <= week * 7 && day > week * 7 - 7){
                        return true;
                    }
                    return false;
                });
            }
        }
    }


    /**
     * alerts user if a nearby (within 15 mins) appointment exists
     */
    public void checkNearAppointment() {
        ZonedDateTime highLimit = ZonedDateTime.now().plusMinutes(15);
        ZonedDateTime lowLimit = ZonedDateTime.now().minusMinutes(15);

        Appointment nearestAppt = null;

        //get next appointment within time limit
        for(Appointment nextAppt: appointments){
            if(nextAppt.getStart().isAfter(lowLimit) && nextAppt.getStart().isBefore(highLimit)){
                if(nearestAppt == null || nextAppt.getStart().isBefore(nearestAppt.getStart())){
                    nearestAppt = nextAppt;
                }
            }
        }

        if(nearestAppt != null){
            FXMLDialogs.customMessage(
                    "Upcoming appointment - ID: " + nearestAppt.getAppointment_ID() +
                            "- date: " + nearestAppt.getStart().toLocalDate() +
                            "- time: " + nearestAppt.getStart().toLocalTime(),
                    "ALERT" );
        } else {
            FXMLDialogs.customMessage(
                    "No upcoming appointment within 15 minutes",
                    "ALERT" );
        }
    }

    /**
     * get customers list from database
     */
    public void updateCustomers(){
        try {
            customers = DBQuery.getCustomers();
            customerTable.setItems(new FilteredList<Customer>(customers, b -> true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * get appointments list from database
     */
    public void updateAppointments(){
        try {
            appointments = DBQuery.getAppointments();
            appointmentsFilteredList = new FilteredList<Appointment>(appointments, b -> true);
            appointmentTable.setItems(appointmentsFilteredList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * shows customers tab and hides other tabs
     *
     * @param event passed from javaFX
     */
    @FXML
    void showCustomersTab(ActionEvent event) {
        turnOnButtons();
        if(!tab.equalsIgnoreCase("customers")){
            tab = "customers";
            customersTab.setVisible(true);
            appointmentsTab.setVisible(false);
            reportsTab.setVisible(false);
        }
    }

    /**
     * shows appointments tab and hides other tabs
     * @param event passed from javaFX
     */
    @FXML
    void showAppointmentsTab(ActionEvent event) {
        turnOnButtons();
        if(!tab.equalsIgnoreCase("appointments")){
            tab = "appointments";
            appointmentsTab.setVisible(true);
            customersTab.setVisible(false);
            reportsTab.setVisible(false);
        }
    }

    /**
     * shows reports tab and hides other tabs
     *
     * @param event passed from javaFX
     */
    @FXML
    void showReportsTab(ActionEvent event) {
        turnOffButtons();
        runReports();
        if(!tab.equalsIgnoreCase("reports")) {
            tab = "reports";
            appointmentsTab.setVisible(false);
            customersTab.setVisible(false);
            reportsTab.setVisible(true);
        }
    }

    /**
     * shows buttons
     */
    public void turnOnButtons(){
        addButton.setVisible(true);
        deleteButton.setVisible(true);
        editButton.setVisible(true);
    }

    /**
     * hides buttons
     */
    public void turnOffButtons(){
        addButton.setVisible(false);
        deleteButton.setVisible(false);
        editButton.setVisible(false);
    }

    /**
     * run reports with latest data
     */
    public void runReports() {
        updateAppointmentsTotal();
        updateSchedule();
        updateCustomersTotal();
    }

    /**
     * updates report total when month filter is adjusted on form
     *
     * @param event provided by JavaFX
     */
    @FXML
    void updateAppointmentTotalReport(ActionEvent event) {
        updateAppointmentsTotal();
    }

    /**
     * updates report total when type filter is adjusted on form
     *
     * @param event provided by JavaFX
     */
    @FXML
    void updateAppointmentTotalReport2(KeyEvent event) {
        updateAppointmentsTotal();
    }

    /**
     * updates 1st report: appointment totals
     */
    public void updateAppointmentsTotal() {
        int num = 0;
        for(Appointment appointment: appointments){
            if(passesMonthCheck(appointment) && passesTypeCheck(appointment)){
                num++;
            }
        }
        totalApptLabel.setText(Integer.toString(num));
    }

    /**
     * determines if appointment should be counted for 1st report based on type
     * @return true if it passes the type check, otherwise false
     * @param appointment appointment to check
     */
    public boolean passesTypeCheck(Appointment appointment) {
        if(typeTextArea.getText().length() == 0 || appointment.getType().contains(typeTextArea.getText())){
            return true;
        }
        return false;
    }

    /**
     * determines if appointment should be counted for 1st report based on month
     * @return true if it passes the month check, otherwise false
     * @param appointment appointment to check
     */
    public boolean passesMonthCheck(Appointment appointment) {
        if(monthComboBox.getSelectionModel().isEmpty() || monthComboBox.getValue().equalsIgnoreCase("ALL")){
            return true;
        } else if(appointment.getStart().getMonth() == Month.valueOf(monthComboBox.getValue())){
            return true;
        } else {
            return false;
        }
    }

    /**
     * updates 2nd report: schedule
     */
    public void updateSchedule(){
        scheduleFilteredList = new FilteredList<>(appointments);
        scheduleTable.setItems(scheduleFilteredList);
        filterScheduleHelper();
    }

    /**
     * fires whenever contact combo box makes a new selection
     *
     * @param event provided by JavaFX
     */
    @FXML
    void filterSchedule(ActionEvent event) {
        filterScheduleHelper();
    }

    /**
     * filters schedule by combo box selection
     *
     * LAMBDA EXPRESSION:
     * used here for an easy to read and edit solution for filtering
     */
    public void filterScheduleHelper(){
        if(contactComboBox.getSelectionModel().getSelectedItem() != null){
            scheduleFilteredList.setPredicate(appointment -> {
                if(appointment.getContact_ID() == contactComboBox.getSelectionModel().getSelectedItem().getContact_ID()){
                    return true;
                }
                return false;
            });
        } else {
            scheduleFilteredList.setPredicate(p -> false);
        }
    }

    /**
     * updates 3rd report: customers totals
     */
    public void updateCustomersTotal() {
        int num = 0;
        for(Customer customer: customers){
            if(passesCountryCheck(customer) && passesStateCheck(customer)){
                num++;
            }
        }
        totalCustomerLabel.setText(Integer.toString(num));
    }

    /**
     * determines if customer should be counted for 3rd report based on state
     * @return true if it passes the state check, otherwise false
     * @param customer customer to check
     */
    public boolean passesStateCheck(Customer customer) {
        if(stateProvinceComboBox.getSelectionModel().isEmpty()){
            return true;
        } else if (customer.getDivision_ID() == stateProvinceComboBox.getSelectionModel().getSelectedItem().getDivision_ID()){
            return true;
        }
        return false;
    }

    /**
     * determines if customer should be counted for 3rd report based on country
     * @return true if it passes the country check, otherwise false
     * @param customer customer to check
     */
    public boolean passesCountryCheck(Customer customer) {
        if(countryComboBox.getSelectionModel().isEmpty()){
            return true;
        }
        Division division = null;
        for(Division nextDiv : divisions){
            if(customer.getDivision_ID() == nextDiv.getDivision_ID()){
                division = nextDiv;
                break;
            }
        }
        if(division != null && division.getCOUNTRY_ID() == countryComboBox.getValue().getCountry_ID()){
            return true;
        }
        return false;
    }

    /**
     * updates report 3 customer totals
     * @param event provided by JavaFX
     */
    @FXML
    void updateCustomerTotalReport(ActionEvent event) {
        updateCustomersTotal();
    }

    /**
     * wipes state province box and updates report 3 customer totals
     * @param event provided by JavaFX
     */
    @FXML
    void updateStateProvinceComboBox(ActionEvent event) {
        stateProvinceComboBox.getSelectionModel().clearSelection();
        filterStateBox();
        updateCustomersTotal();
    }

    /**
     * filters state box by current country box
     */
    public void filterStateBox() {
        stateProvinceFilteredList.setPredicate(division -> {
            if(division.getCOUNTRY_ID() == countryComboBox.getSelectionModel().getSelectedItem().getCountry_ID()){
                return true;
            }

            return false;
        });
    }

    /**
     * getter
     *
     * @return customers list
     */
    public ObservableList<Customer> getCustomers(){
        return customers;
    }

    /**
     * getter
     *
     * @return divisions list
     */
    public ObservableList<Division> getDivisions() {
        return divisions;
    }

    /**
     * getter
     *
     * @return countries list
     */
    public ObservableList<Country> getCountries() {
        return countries;
    }

    /**
     * getter
     *
     * @return contacts list
     */
    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    /**
     * getter
     *
     * @return users list
     */
    public ObservableList<User> getUsers() {
        return users;
    }

    /**
     * getter
     *
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * getter
     *
     * @return user who logged in to software
     */
    public User getUser() {
        return user;
    }

    /**
     * getter
     *
     * @return appointments list
     */
    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }
}
