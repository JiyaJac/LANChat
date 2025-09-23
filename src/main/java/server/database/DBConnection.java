package server.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
    private Connection c;
    private Statement st;
    private ResultSet rs;

    private final String url = "jdbc:mysql://localhost:3306/lanchat";
    private final String username = "root";
    private final String password = "hehehehe";

    public Map<String, String> userCredentials = new HashMap<>();

    // Constructor: load driver + establish connection
    public DBConnection() throws ClassNotFoundException, SQLException {
        // Load Type-4 MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Establish connection
        c = DriverManager.getConnection(url, username, password);

        // Create a statement
        st = c.createStatement();
    }


    public Connection getC() {
        return c;
    }


    // Fetch all users into userCredentials
//    public void fetchUsers() throws SQLException {
//        String query1 = "SELECT * FROM USERS";
//
//        try {
//            rs = st.executeQuery(query1);
//
//            // Process result set
//            while (rs.next()) {
//                String name = rs.getString("user_name");
//                String pswd = rs.getString("password");
//                userCredentials.put(name, pswd);
//            }
//        } finally {
//            // Always close result set
//            if (rs != null) rs.close();
//        }
//    }
}
