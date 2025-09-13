package database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DBConnection {
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

    public Map<String, String> userCredentials = new HashMap<>();

    // Establish JDBC DBConnection
    public void fetchUsers() throws ClassNotFoundException, SQLException {
        ResultSet rs;
        Statement st;
        Connection c;
        try {

            // Load Type-4 Driver
            // MySQL Type-4 driver class
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            c = DriverManager.getConnection(
                    url, username, password);

            // Create a statement
            st = c.createStatement();

//            int count=st.executeUpdate(query2);
//            System.out.println(count+"rows changed");

            // Execute the query1
            rs = st.executeQuery(query1);

            // Process result set
            while (rs.next()) {
                String name = rs.getString("user_name");
                String pswd = rs.getString("password");
                userCredentials.put(name, pswd);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // Close the connection
        rs.close();
        st.close();
        c.close();
        System.out.println("DBConnection closed.");
    }
}

//    } catch (ClassNotFoundException e) {
//            System.err.println("JDBC Driver not found: " + e.getMessage());
//        } catch (SQLException e) {
//            System.err.println("SQL Error: " + e.getMessage());
//        }
//    }
//}
