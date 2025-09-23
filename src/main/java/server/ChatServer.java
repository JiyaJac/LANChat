package server;

import server.database.DBConnection;
import server.messages.GroupMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


import static server.net.ConvoServer.clientOutputs;


public class ChatServer{
    DBConnection db;

    public ChatServer() throws SQLException, ClassNotFoundException {
        this.db = new DBConnection();
    }


    public String login(String line, PrintStream ps) {
        Connection c = db.getC();
        String[] parts = line.split(":");
        if (parts.length == 3) {
            String user = parts[1];
            String pass = parts[2];

            try (
                 PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE user_name=? AND password=?")) {

                stmt.setString(1, user);
                stmt.setString(2, pass);


                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        synchronized (clientOutputs) {
                            if (clientOutputs.containsKey(user)) {
                                ps.println("LOGIN_FAILED: User already logged in");
                                return null;
                            }
                            clientOutputs.put(user, ps);
                        }
                        ps.println("LOGIN_SUCCESS");
                        broadcastOnlineUsers();
                        System.out.println("Login successful");
                        clientOutputs.put(user, ps);
                        return user;
                    } else {
                        ps.println("LOGIN_FAILED");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ps.println("LOGIN_FAILED");
            }
        }
        else {
            ps.println("LOGIN_FAILED"); // wrong format
        }
        System.out.println("Login failed");
        return null;
    }



    public String signup(String line, PrintStream ps) throws SQLException {
        String[] parts = line.split(":");
        if (parts.length == 3) {
            String user = parts[1];
            String pass = parts[2];

            try (

                Connection c=db.getC();

                PreparedStatement checkStmt = c.prepareStatement("SELECT * FROM users WHERE user_name = ?");
                PreparedStatement insertStmt = c.prepareStatement("INSERT INTO users (user_name, password) VALUES (?, ?)")) {

                // Check if username already exists
                checkStmt.setString(1, user);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    ps.println("SIGNUP_FAILED"); // username exists
                } else {
                    // Insert new user
                    insertStmt.setString(1, user);
                    insertStmt.setString(2, pass);
                    insertStmt.executeUpdate();

                    ps.println("SIGNUP_SUCCESS"); // ✅ tell client
                    broadcastOnlineUsers();
                    System.out.println("✅ New user registered: " + user);
                    clientOutputs.put(user, ps);
                    return user;
                }
                rs.close();
            }
        }
        else {
            ps.println("SIGNUP_FAILED"); // wrong format
        }
        return null;
    }


    public static void broadcastOnlineUsers() {
        synchronized (clientOutputs) {
            String users = String.join(",", clientOutputs.keySet());

            for (PrintStream ps : clientOutputs.values()) {
                ps.println("ONLINE:" + users);
            }
        }
    }

    public static void removeClient(Socket socket) {
        String toRemove = null;

        synchronized (clientOutputs) {
            for (Map.Entry<String, PrintStream> entry : clientOutputs.entrySet()) {
                if (entry.getValue().checkError()) {
                    toRemove = entry.getKey();
                    break;
                }
            }
            if (toRemove != null) {
                clientOutputs.remove(toRemove);
                broadcastOnlineUsers();
                GroupMessage grp=new GroupMessage(toRemove,"❌ " + toRemove + " left the chat.");
                grp.send();
            }
        }

        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public void logout(String line, PrintStream ps) {
        String[] parts = line.split(":"); // expected format: "LOGOUT:username"
        if (parts.length == 2) {
            String user = parts[1];

            // Remove user from the active clients map
            if (clientOutputs.containsKey(user)) {
                clientOutputs.remove(user);
                System.out.println("✅ User logged out: " + user);
            }

            // Broadcast updated online users to all remaining clients
            broadcastOnlineUsers();
        } else {
            ps.println("LOGOUT_FAILED"); // invalid format
        }
    }


}
