package server.messages;

import server.messages.Message;
import static server.net.ConvoServer.clientOutputs;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class GroupMessage implements Message {
    private final String sender;
    private final String content;

    public GroupMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    @Override
    public void send() {

        //save to database
        String sql = "INSERT INTO broadcast_chat_history (sender_id, message_text, sent_at) " +
                "VALUES ((SELECT id FROM users WHERE user_name = ?), ?, NOW())";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lanchat", "root", "hehehehe");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sender);
            stmt.setString(2, content);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        synchronized (clientOutputs) {
            for (Map.Entry<String, PrintStream> entry : clientOutputs.entrySet()) {
                entry.getValue().println("MSG:" + sender + ":" + content);
            }
        }
    }
}
