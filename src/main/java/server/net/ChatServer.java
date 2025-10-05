package server.net;



import server.database.DBConnection;
import server.messages.GroupMessage;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.util.Map;
import static server.net.ConvoServer.clientOutputs;


public class ChatServer{
    DBConnection db;

    public ChatServer() throws SQLException, ClassNotFoundException {
        this.db = new DBConnection();
    }


    public String login(String line, PrintStream ps) {

        String[] parts = line.split(":");
        if (parts.length == 3) {
            String user = parts[1];
            String pass = parts[2];

            try (
                    Connection c = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/lanchat", "root", "hehehehe");
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
                    Connection c = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/lanchat", "root", "hehehehe");

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
                    broadcastOnlineUsers();
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

            System.out.println("📢 Broadcasting online users: " + users);

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

    public void sendPrivateChatHistory(String user1, String user2) {
        String sql = "SELECT u1.user_name AS sender, u2.user_name AS receiver, message_text, sent_at " +
                "FROM private_chat_history p " +
                "JOIN users u1 ON p.sender_id = u1.id " +
                "JOIN users u2 ON p.receiver_id = u2.id " +
                "WHERE (u1.user_name=? AND u2.user_name=?) OR (u1.user_name=? AND u2.user_name=?) " +
                "ORDER BY sent_at ASC";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lanchat", "root", "hehehehe");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);

            ResultSet rs = stmt.executeQuery();
            PrintStream ps = clientOutputs.get(user1);

            if (ps != null) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String receiver = rs.getString("receiver"); // important!
                    String content = rs.getString("message_text");

                    ps.println("PRIVATE_HISTORY:" + sender + ":" + receiver + ":" + content);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void sendBroadcastHistory(String user) {
        String sql = "SELECT u.user_name AS sender, message_text, sent_at " +
                "FROM broadcast_chat_history b " +
                "JOIN users u ON b.sender_id = u.id " +
                "ORDER BY sent_at ASC";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lanchat", "root", "hehehehe");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            PrintStream ps = clientOutputs.get(user);

            if (ps != null) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String content = rs.getString("message_text");
                    ps.println("BROADCAST_HISTORY:" + sender + ":" + content);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
