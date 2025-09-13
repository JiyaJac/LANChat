package database;

import java.sql.*;
import java.util.Vector;

public class Connection {
    // Database URL, username, and password

    // Replace with your database name
    String url = "jdbc:mysql://localhost:3306/lanchat";

    // Replace with your MySQL username
    String username = "root";

    // Replace with your MySQL password
    String password = "hehehehe";

    // Updated query1 syntax for modern databases
    String query1 = "SELECT * FROM USERS";
//        String query2="INSERT INTO STUDENTS (id,name) VALUES (0,'orion')";

    Vector<String> password_list = new Vector<>();
    Vector<String> users_list = new Vector<>();

    // Establish JDBC Connection
    public void fetchUsers() {
        try {

            // Load Type-4 Driver
            // MySQL Type-4 driver class
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            java.sql.Connection c = DriverManager.getConnection(
                    url, username, password);

            // Create a statement
            Statement st = c.createStatement();

//            int count=st.executeUpdate(query2);
//            System.out.println(count+"rows changed");

            // Execute the query1
            ResultSet rs = st.executeQuery(query1);

            // Process result set
            while (rs.next()) {
                // Assuming STUDENTS table has id, name, age columns
                String pswd = rs.getString("password");
                password_list.add(pswd);
                String name = rs.getString("user_name");
                users_list.add(name);
            }

            // Close the connection
            rs.close();
            st.close();
            c.close();
            System.out.println("Connection closed.");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
}
