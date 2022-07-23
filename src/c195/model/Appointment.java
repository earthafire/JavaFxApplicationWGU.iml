/**
 * c195.model contains model objects modeling the database
 */
package c195.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private final int Appointment_ID;
    private String Title;
    private String Description;
    private String Location;
    private String Type;
    private ZonedDateTime Start;
    private ZonedDateTime End;
    private int Customer_ID;
    private int User_ID;
    private int Contact_ID;

    /**
     *
     * @param appointment_ID unique id in "appointments" table
     * @param title name of appointment
     * @param description description of appointment
     * @param location location of appointment
     * @param type type of appointment
     * @param start ZonedDateTime representing beginning of appointment
     * @param end ZonedDateTime representing end of appointment
     * @param customer_ID unique id of customer involved in "customers"
     * @param user_ID unique id of user involved in "users" table
     * @param contact_ID unique id of contact information in "contacts" table
     */
    public Appointment(int appointment_ID, String title, String description, String location, String type, ZonedDateTime start, ZonedDateTime end, int customer_ID, int user_ID, int contact_ID) {
        Appointment_ID = appointment_ID;
        Title = title;
        Description = description;
        Location = location;
        Type = type;
        Start = start;
        End = end;
        Customer_ID = customer_ID;
        User_ID = user_ID;
        Contact_ID = contact_ID;
    }

    public int getAppointment_ID() {
        return Appointment_ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public ZonedDateTime getStart() {
        return Start;
    }

    public String getStartSQL(){
        LocalDateTime instant = LocalDateTime.ofInstant(getStart().toInstant(), ZoneOffset.UTC);
        return instant.format(DateTimeFormatter.ISO_LOCAL_DATE) + " "  + instant.format(DateTimeFormatter.ISO_LOCAL_TIME).substring(0,8);
    }

    public void setStart(ZonedDateTime start) {
        Start = start;
    }

    public ZonedDateTime getEnd() {
        return End;
    }

    public String getEndSQL(){
        LocalDateTime instant = LocalDateTime.ofInstant(getEnd().toInstant(), ZoneOffset.UTC);
        return instant.format(DateTimeFormatter.ISO_LOCAL_DATE) + " "  + instant.format(DateTimeFormatter.ISO_LOCAL_TIME).substring(0,8);
    }

    public void setEnd(ZonedDateTime end) {
        End = end;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public int getContact_ID() {
        return Contact_ID;
    }

    public void setContact_ID(int contact_ID) {
        Contact_ID = contact_ID;
    }

    /**
     * get the values needed to call an Insert SQL command
     * mirrors "appointments" database but leaves out appointment_id for database to make
     * @param creator name of user who is creating the object
     * @return String ready to put in statement
     */
    public String getInsertString(String creator){
        //create sql statement
        return "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) values(" +
                "'" + getTitle() + "'" + ", " +
                "'" + getDescription() + "'" + ", " +
                "'" + getLocation() + "'" + ", " +
                "'" + getType() + "'" + ", " +
                "'" + getStartSQL() + "'" + ", " +
                "'" + getEndSQL() + "'" + ", " +
                "NOW()"  + ", " +
                "'" + creator + "'" + ", " +
                "NOW()" + ", " +
                "'" + creator + "'" + ", " +
                getCustomer_ID()+ ", " +
                getUser_ID() + ", " +
                getContact_ID() + ")";
    }

    /**
     * get the values needed to call an Update SQL command
     * @param creator name of user who is updating the object
     * @return String ready to put in statement
     */
    public String getUpdateString(String creator){
        //fields to set: Title, Description, Location, Type, Start, End, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID
        return "UPDATE appointments SET " +
                "Title = '" + getTitle() + "', " +
                "Description = '" + getDescription() + "', " +
                "Location = '" + getLocation() + "', " +
                "Type = '" + getType() + "', " +
                "Start = '" + getStartSQL() + "', " +
                "End = '" + getEndSQL() + "', " +
                "Customer_ID = " + getCustomer_ID() + ", " +
                "User_ID = " + getUser_ID() + ", " +
                "Contact_ID = " + getContact_ID() + ", " +
                "Last_Update = NOW(), " +
                "Last_Updated_By = '" + creator + "' " +
                "WHERE Appointment_ID = " + getAppointment_ID();
    }

    /**
     * get the values needed to call a Delete SQL command to remove
     * this appointment from 'appointements' table
     * @return String ready to put in statement
     */
    public String getDeleteString(){
        return "DELETE FROM appointments WHERE Appointment_ID=" + getAppointment_ID();
    }
}
