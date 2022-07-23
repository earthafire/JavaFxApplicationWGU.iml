package c195;

import c195.model.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.DBQuery;
import utils.FXMLDialogs;

import java.time.*;

/**
 * Appointment "edit" and "add" page logic
 * @author Addison Ashworth
 */
public class ControllerAppointment {

    private ControllerMain controllerMain;

    @FXML
    private Label title;

    @FXML
    private Label Appointment_IDLabel;

    @FXML
    private TextField TitleTextField;

    @FXML
    private TextField DescriptionTextField;

    @FXML
    private TextField LocationTextField;

    @FXML
    private TextField TypeTextField;

    @FXML
    private DatePicker StartDatePicker;

    @FXML
    private TextField StartTimeHourTextField;

    @FXML
    private TextField StartTimeMinuteTextField;

    @FXML
    private TextField EndTimeHourTextField;

    @FXML
    private TextField EndTimeMinuteTextField;

    @FXML
    private DatePicker EndDatePicker;

    @FXML
    private ComboBox<Customer> CustomerComboBox;

    @FXML
    private ComboBox<Contact> ContactComboBox;

    @FXML
    private ComboBox<User> UserComboBox;

    @FXML
    private Button submitButton;

    /**
     * prepares the form for an edit, filling out all the fields from the old appointment object
     *
     * @param controllerMain to pull data from last window
     * @param appointment appointment to edit
     */
    void editAppointment(ControllerMain controllerMain, Appointment appointment){
        title.setText("Edit");
        this.controllerMain = controllerMain;
        Appointment_IDLabel.setText(String.valueOf(appointment.getAppointment_ID()));
        TitleTextField.setText(appointment.getTitle());
        DescriptionTextField.setText(appointment.getDescription());
        LocationTextField.setText(appointment.getLocation());
        TypeTextField.setText(appointment.getType());

        //set combo box values
        setComboBoxes(controllerMain.getCustomers(), controllerMain.getUsers(), controllerMain.getContacts());

        //set combo box selected values
        for(Customer customer: controllerMain.getCustomers()) {
            if (customer.getCustomer_ID() == appointment.getCustomer_ID()) {
                CustomerComboBox.getSelectionModel().select(customer);
                break;
            }
        }

        for(User user: controllerMain.getUsers()) {
            if (user.getUser_ID() == appointment.getUser_ID()) {
                UserComboBox.getSelectionModel().select(user);
                break;
            }
        }

        for(Contact contact: controllerMain.getContacts()) {
            if (contact.getContact_ID() == appointment.getContact_ID()) {
                ContactComboBox.getSelectionModel().select(contact);
                break;
            }
        }

        //set date-times
        StartDatePicker.setValue(appointment.getStart().toLocalDate());
        EndDatePicker.setValue(appointment.getEnd().toLocalDate());

        StartTimeHourTextField.setText(appointment.getStart().getHour() + "");
        StartTimeMinuteTextField.setText(appointment.getStart().getMinute() + "");
        EndTimeHourTextField.setText(appointment.getEnd().getHour() + "");
        EndTimeMinuteTextField.setText(appointment.getEnd().getMinute() + "");

    }

    /**
     * prepares the form by giving you a blank sheet to make a new appointment on
     *
     * @param controllerMain previous form to pull data from
     */
    void addAppointment(ControllerMain controllerMain){
        title.setText("Add");
        this.controllerMain = controllerMain;
        setComboBoxes(controllerMain.getCustomers(), controllerMain.getUsers(), controllerMain.getContacts());
    }

    /**
     * submits form if valid when "submit" button is pressed
     *
     * @param event supplied by JAVAFX
     */
    @FXML
    void submit(ActionEvent event) {
        if(isFormValid()){
            //if there is a previous appointment id, get it
            int appointmentID;
            if(Appointment_IDLabel.getText().length() < 1){
                appointmentID = 0;
            } else {
                appointmentID = Integer.parseInt(Appointment_IDLabel.getText());
            }

            ZoneId zid = ZoneId.systemDefault();

            Appointment tempAppointment = new Appointment(
                    appointmentID,
                    TitleTextField.getText(),
                    DescriptionTextField.getText(),
                    LocationTextField.getText(),
                    TypeTextField.getText(),
                    getStartDateFromForm(),
                    getEndDateFromForm(),
                    CustomerComboBox.getSelectionModel().getSelectedItem().getCustomer_ID(),
                    UserComboBox.getSelectionModel().getSelectedItem().getUser_ID(),
                    ContactComboBox.getSelectionModel().getSelectedItem().getContact_ID());

            if(!schedulesConflict(tempAppointment, controllerMain.getAppointments())) {
                if (title.getText().equals("Add")) {
                    String statement = tempAppointment.getInsertString(controllerMain.getUser().getUser_Name());
                    System.out.println(statement);
                    DBQuery.executeStatement(statement);
                } else if (title.getText().equals("Edit")) {
                    String statement = tempAppointment.getUpdateString(controllerMain.getUser().getUser_Name());
                    System.out.println(statement);
                    DBQuery.executeStatement(statement);
                } else {
                    System.out.println("I shouldn't ever print!");
                }
                controllerMain.updateAppointments();

                Stage stage = (Stage) title.getScene().getWindow();
                stage.close();
            } else {
                FXMLDialogs.error("Scheduling conflict");
            }
        }
    }

    /**
     *  checks all appointments in observable list against given appointment for contflicts.
     *  returns true if it conflicts, false if it does not
     *
     * @param appointment single appointment potentially to add
     * @param appointments appointments to check against
     * @return true if conflicts, false if no conflicts
     */
    private boolean schedulesConflict(Appointment appointment, ObservableList<Appointment> appointments) {
        for(Appointment nextAppt: appointments){

            //appointment cant conflict with itself
            if(appointment.getAppointment_ID() != nextAppt.getAppointment_ID()){

                //if they have same customer id check for conflicts
                if(appointment.getCustomer_ID() == nextAppt.getCustomer_ID()){
                    System.out.println("check for overlaps!");
                    if(overlaps(appointment, nextAppt)){
                        return true;
                    }
                }
            }
        }

        //if no conflicts found, return false
        return false;
    }

    /**
     * checks if two appointments occur during the same time.
     * if they do, return true. else false.
     *
     * @return true if conflict, false if no conflicts
     *
     * @param appointment1 appointment to check against
     * @param appointment2 appointment to check against
     */
    public boolean overlaps(Appointment appointment1, Appointment appointment2){
        if(appointment1.getStart().isBefore(appointment2.getEnd()) && appointment2.getStart().isBefore(appointment1.getEnd())){
            System.out.println(appointment1.getAppointment_ID() + " conflicts with " + appointment2.getAppointment_ID());
            return true;
        }

        return false;
    }

    /**
     * helper function to add the values from lists to combo boxes
     * @param customers customer combo box values
     * @param users user combo box values
     * @param contacts contact combo box values
     */
    void setComboBoxes(ObservableList<Customer> customers, ObservableList<User> users, ObservableList<Contact> contacts){
        CustomerComboBox.setItems(customers);
        CustomerComboBox.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer.getCustomer_Name();
            }

            @Override
            public Customer fromString(String s) {
                return null;
            }
        });

        UserComboBox.setItems(users);
        UserComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user.getUser_Name();
            }

            @Override
            public User fromString(String s) {
                return null;
            }
        });

        ContactComboBox.setItems(contacts);
        ContactComboBox.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                return contact.getContact_Name();
            }

            @Override
            public Contact fromString(String s) {
                return null;
            }
        });
    }

    /**
     * check if user input values are valid
     * @return true if valid, false if invalid
     */
    public boolean isFormValid(){
        if(TitleTextField.getText().length() < 1){
            fieldNotValidError("Title");
            return false;
        } else if (DescriptionTextField.getText().length() < 1){
            fieldNotValidError("Description");
            return false;
        } else if (LocationTextField.getText().length() < 1){
            fieldNotValidError("Location");
            return false;
        } else if (TypeTextField.getText().length() < 1){
            fieldNotValidError("Type");
            return false;
        } else if (StartDatePicker.getValue().toString() == null){
            fieldNotValidError("Start Date");
            return false;
        } else if (StartTimeHourTextField.getText().length() < 1 || !canBeParsedToInt(StartTimeHourTextField.getText())){
            fieldNotValidError("Start Time Hour");
            return false;
        } else if (StartTimeMinuteTextField.getText().length() < 1 || !canBeParsedToInt(StartTimeMinuteTextField.getText())){
            fieldNotValidError("Start Time Minute");
            return false;
        } else if (EndDatePicker.getValue().toString() == null){
            fieldNotValidError("End Date");
            return false;
        } else if (EndTimeHourTextField.getText().length() < 1 || !canBeParsedToInt(EndTimeHourTextField.getText())){
            fieldNotValidError("End Time Hour");
            return false;
        } else if (EndTimeMinuteTextField.getText().length() < 1 || !canBeParsedToInt(EndTimeMinuteTextField.getText())){
            fieldNotValidError("End Time Minute");
            return false;
        } else if (CustomerComboBox.getSelectionModel().isEmpty()){
            fieldNotValidError("Customer");
            return false;
        } else if (UserComboBox.getSelectionModel().isEmpty()){
            fieldNotValidError("User");
            return false;
        } else if (ContactComboBox.getSelectionModel().isEmpty()){
            fieldNotValidError("Contact");
            return false;
        }
        ZonedDateTime startZDT = getStartDateFromForm();
        ZonedDateTime endZDT = getEndDateFromForm();
        if (endZDT.isBefore(startZDT)){
            switch (controllerMain.getLanguage()) {
                case "fr":
                    FXMLDialogs.error("DateTime de début sont après DateTime de fin!");
                    break;
                default:
                    FXMLDialogs.error("Start DateTime is after End DateTime!");
                    break;
            }
            return false;
        } else if (!inBusinessHours(startZDT, endZDT)){
            switch (controllerMain.getLanguage()) {
                case "fr":
                    FXMLDialogs.error("Rendez-vous est en dehors des heures d'ouverture!");
                    break;
                default:
                    FXMLDialogs.error("Appointment is outside business hours!");
                    break;
            }
            return false;
        }

        return true;
    }

    /**
     * checks if form dates are within business hours
     * (assume start date is before end date)
     *
     * @param startZDT start of appointment time
     * @param endZDT end of appointment time
     * @return true if in business hours, false if outside
     */
    public boolean inBusinessHours(ZonedDateTime startZDT, ZonedDateTime endZDT){
        ZonedDateTime startEST = startZDT.withZoneSameInstant(ZoneId.of("-05:00"));
        ZonedDateTime endEST = endZDT.withZoneSameInstant(ZoneId.of("-05:00"));
        System.out.println(startEST);
        System.out.println(endEST);
        if(startEST.getHour() < 8 || startEST.getHour()  > 22){
            return false;
        }
        if(endEST.getHour() < 8 || endEST.getHour()  > 22){
            return false;
        }
        if(endEST.getHour() < startEST.getHour()){
            return false;
        }
        return true;
    }

    /**
     * helper function to determine validity of integer from string
     * @param string string that could be an int
     * @return true if value could be int, false if not
     */
    public boolean canBeParsedToInt(String string){
        try{
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * helper function for translating similar messages about fields being empty
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

    public ZonedDateTime getStartDateFromForm(){
        return ZonedDateTime.of(
                LocalDateTime.of(
                        StartDatePicker.getValue(),
                        LocalTime.of(
                                //Hour
                                Integer.parseInt(StartTimeHourTextField.getText()),
                                //Minute
                                Integer.parseInt(StartTimeMinuteTextField.getText()))),
                //Zone
                ZoneId.systemDefault());
    }

    public ZonedDateTime getEndDateFromForm(){
        return ZonedDateTime.of(
                LocalDateTime.of(
                        EndDatePicker.getValue(),
                        LocalTime.of(
                                //Hour
                                Integer.parseInt(EndTimeHourTextField.getText()),
                                //Minute
                                Integer.parseInt(EndTimeMinuteTextField.getText()))),
                //Zone
                ZoneId.systemDefault());
    }
}
