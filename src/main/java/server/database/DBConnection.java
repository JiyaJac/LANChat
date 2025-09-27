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

//    public void close() throws SQLException {
//        if (c != null && !c.isClosed()) c.close();
//    }
}
