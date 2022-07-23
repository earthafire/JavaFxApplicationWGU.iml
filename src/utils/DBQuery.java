package utils;

import c195.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * utilizes DBConnection to query database for
 * information and convert it to model
 */
public class DBQuery {

    private static Statement statement;

    public static void setStatement(Connection conn) throws SQLException {
        statement = conn.createStatement();
    }

    /**
     * gets singleton pattern Statement object
     * @return singleton Statement
     */
    public static Statement getStatement(){
        return statement;
    }


    public static int executeStatement(String selectStatement){
        try {
            statement.execute(selectStatement);
            return statement.getUpdateCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * returns a User object based on Connection created on software
     * launch and user input
     *
     * @param user_name User_Name user input
     * @param password Password user input
     * @return User object represented in first row returned
     * @throws SQLException if problem with connection, throws error
     */
    public static User getUser(String user_name, String password) throws SQLException {
        String selectStatement = "SELECT * FROM users WHERE User_Name='" + user_name + "' AND Password='" + password + "'";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();
        if(results.next()){
            return new User(
                    results.getInt("User_ID"),
                    results.getString("User_Name"),
                    results.getString("Password"));
        }
        return null;
    }

    /**
     * returns a list of all countries in database based on Connection
     * created on software launch and user input
     *
     * @return "countries" table data from database
     * @throws SQLException if problem with connection, throws error
     */
    public static ObservableList<Country> getCountries() throws SQLException {
        ObservableList<Country> answer = FXCollections.observableArrayList();

        String selectStatement = "SELECT * FROM countries";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();
        while(results.next()){
            LocalDateTime Create_Date_ldt = LocalDateTime.of(
                    results.getDate("Create_Date").toLocalDate(),
                    results.getTime("Create_Date").toLocalTime());

            LocalDateTime Last_Update_ldt = LocalDateTime.of(
                    results.getDate("Last_Update").toLocalDate(),
                    results.getTime("Last_Update").toLocalTime());

            answer.add(new Country(
                    results.getInt("Country_ID"),
                    results.getString("Country"),
                    Create_Date_ldt,
                    results.getString("Created_By"),
                    Last_Update_ldt,
                    results.getString("Last_Updated_By")));
        }
        return answer;
    }

    /**
     * returns a list of all customers in database based on Connection
     * created on software launch and user input
     *
     * @return "customers" table data from database
     * @throws SQLException if problem with connection, throws error
     */
    public static ObservableList<Customer> getCustomers() throws SQLException {
        ObservableList<Customer> answer = FXCollections.observableArrayList();

        String selectStatement = "SELECT * FROM customers";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();
        while(results.next()){
            answer.add(new Customer(
                    results.getInt("Customer_ID"),
                    results.getString("Customer_Name"),
                    results.getString("Address"),
                    results.getString("Postal_Code"),
                    results.getString("Phone"),
                    results.getInt("Division_ID")));
        }
        return answer;
    }

    /**
     * returns a list of all divisions in database based on Connection
     * created on software launch and user input
     *
     * @return "first_level_divisions" table data from database
     * @throws SQLException if problem with connection, throws error
     */
    public static ObservableList<Division> getDivisions() throws SQLException {
        ObservableList<Division> answer = FXCollections.observableArrayList();

        String selectStatement = "SELECT * FROM first_level_divisions";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();
        while(results.next()){
            LocalDateTime Create_Date_ldt = LocalDateTime.of(
                    results.getDate("Create_Date").toLocalDate(),
                    results.getTime("Create_Date").toLocalTime()) ;

            LocalDateTime Last_Update_ldt = LocalDateTime.of(
                    results.getDate("Last_Update").toLocalDate(),
                    results.getTime("Last_Update").toLocalTime()) ;

            answer.add(new Division(
                    results.getInt("Division_ID"),
                    results.getString("Division"),
                    Create_Date_ldt,
                    results.getString("Created_By"),
                    Last_Update_ldt,
                    results.getString("Last_Updated_By"),
                    results.getInt("COUNTRY_ID")));
        }
        return answer;
    }

    /**
     * returns a list of all contacts in database based on Connection
     * created on software launch and user input
     *
     * @return "contacts" table data from database
     * @throws SQLException if problem with connection, throws error
     */
    public static ObservableList<Contact> getContacts() throws SQLException {
        String selectStatement = "SELECT * FROM contacts";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();

        ObservableList<Contact> answer = FXCollections.observableArrayList();
        while(results.next()){
            answer.add(new Contact(
                    results.getInt("Contact_ID"),
                    results.getString("Contact_Name"),
                    results.getString("Email")));
        }

        return answer;
    }

    /**
     * returns a list of all users in database based on Connection
     * created on software launch and user input
     *
     * @return "users" table data from database
     * @throws SQLException if problem with connection, throws error
     */
    public static ObservableList<User> getUsers() throws SQLException {
        String selectStatement = "SELECT * FROM users";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();

        ObservableList<User> answer = FXCollections.observableArrayList();
        while(results.next()){
            answer.add(new User(
                    results.getInt("User_ID"),
                    results.getString("User_Name"),
                    results.getString("Last_Updated_By")));
        }

        return answer;
    }

    /**
     * returns a list of all users in database based on Connection
     * created on software launch and user input
     *
     * @return "users" table data from database
     * @throws SQLException if problem with connection, throws error
     */
    public static ObservableList<Appointment> getAppointments() throws SQLException {
        String selectStatement = "SELECT * FROM appointments";
        statement.execute(selectStatement);
        ResultSet results = statement.getResultSet();

        ObservableList<Appointment> answer = FXCollections.observableArrayList();
        ZoneId zid = ZoneId.systemDefault();

        while(results.next()){

            LocalDateTime sldt = results.getTimestamp("Start").toLocalDateTime();
            ZonedDateTime startZDT = sldt.atZone(zid);

            LocalDateTime eldt = results.getTimestamp("End").toLocalDateTime();
            ZonedDateTime endZDT = eldt.atZone(zid);

            answer.add(new Appointment(
                    results.getInt("Appointment_ID"),
                    results.getString("Title"),
                    results.getString("Description"),
                    results.getString("Location"),
                    results.getString("Type"),
                    startZDT,
                    endZDT,
                    results.getInt("Customer_ID"),
                    results.getInt("User_ID"),
                    results.getInt("Contact_ID")));
        }

        return answer;
    }
}
