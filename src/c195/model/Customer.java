package c195.model;

public class Customer {
    private final int Customer_ID;
    private String Customer_Name;
    private String Address;
    private String Postal_Code;
    private String Phone;
    private int Division_ID;

    public Customer(int customer_ID, String customer_Name, String address, String postal_Code, String phone, int division_ID) {
        Customer_ID = customer_ID;
        Customer_Name = customer_Name;
        Address = address;
        Postal_Code = postal_Code;
        Phone = phone;
        Division_ID = division_ID;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPostal_Code() {
        return Postal_Code;
    }

    public void setPostal_Code(String postal_Code) {
        Postal_Code = postal_Code;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getDivision_ID() {
        return Division_ID;
    }

    public void setDivision_ID(int division_ID) {
        Division_ID = division_ID;
    }

    /**
     * get the values needed to call an Insert SQL command
     * @param creator name of person who is creating the object
     * @return String ready to put in statement
     */
    public String getInsertString(String creator){
        //mirrors (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, LastUpdate, Last_Updated_By, Division_ID)
        return "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) values(" +
                "'" + getCustomer_Name() + "'" + ", " +
                "'" + getAddress() + "'" + ", " +
                "'" + getPostal_Code() + "'" + ", " +
                "'" + getPhone() + "'" + ", " +
                "NOW()"  + ", " +
                "'" + creator + "'" + ", " +
                "NOW()" + ", " +
                "'" + creator + "'" + ", " +
                getDivision_ID() + ")";
    }

    /**
     * get the values needed to call an Update SQL command
     * @param creator name of user who is updating the object
     * @return String ready to put in statement
     */
    public String getUpdateString(String creator){
        //fields to set: Customer_Name, Address, Postal_Code, Phone, LastUpdate, Last_Updated_By, Division_ID
        return "UPDATE customers SET " +
                "Customer_Name = '" + getCustomer_Name() + "', " +
                "Address = '" + getAddress() + "', " +
                "Postal_Code = '" + getPostal_Code() + "', " +
                "Phone = '" + getPhone() + "', " +
                "Last_Update = " + "NOW()"  + ", " +
                "Last_Updated_By = '" + creator + "', " +
                "Division_ID = " + getDivision_ID() + " " +
                "WHERE Customer_ID = " + getCustomer_ID();
    }

    /**
     * get the values needed to call a Delete SQL command to remove
     * this customer from 'customers' table
     * @return String ready to put in statement
     */
    public String getDeleteString(){
        return "DELETE FROM customers WHERE Customer_ID=" + getCustomer_ID();
    }

    /**
     * get the values needed to call a Delete SQL command to remove
     * all appointments related to this customer from 'appointements' table
     * @return String ready to put in statement
     */
    public String getDeleteAllAppointementString(){
        return "DELETE FROM appointments WHERE Customer_ID=" + getCustomer_ID();
    }
}
