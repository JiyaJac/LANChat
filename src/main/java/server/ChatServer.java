package server;

import database.DBConnection;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static net.ConvoServer.clientOutputs;


public class ChatServer{
    Set<String> s = new TreeSet<>();
    HashMap<String, PrivateRoom> mappings = new HashMap<>();
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



    public String signup(String line, PrintStream ps) throws SQLException, ClassNotFoundException {
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

    public static void broadcast(String message, String sender) {
        synchronized (clientOutputs) {
            for (Map.Entry<String, PrintStream> entry : clientOutputs.entrySet()) {
                if (entry.getKey()!=null) {
                    entry.getValue().println("MSG:" + message);
                }
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
                broadcast("❌ " + toRemove + " left the chat.", toRemove);
            }
        }

        try {
            socket.close();
        } catch (IOException ignored) {}
    }


    public String createPrivate(String u1, String u2) {
        if (!(s.contains(u1) && s.contains(u2))) {
            System.out.println("Both of the users are not present");
            return null;
        }
        PrivateRoom pr = new PrivateRoom(u1, u2);
        if (!(mappings.containsKey(pr.getId()))) {
            mappings.put(pr.getId(), pr);
        }
        return pr.getId();
    }

    public void sendMessage(String id, String msg) {
        PrivateRoom room = mappings.get(id);
        if (room != null) {
            room.sendMessage(msg);
        } else {
            System.out.println("Room with id " + id + " does not exist");
        }
    }
}
