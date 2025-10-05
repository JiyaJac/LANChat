package server.messages;


import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static server.net.ConvoServer.clientOutputs;

public class PrivateMessage implements Message {
    private final String sender;
    private final String recipient;
    private final String content;

    public PrivateMessage(String sender, String recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    @Override
    public void send() {
        synchronized (clientOutputs) {
            PrintStream senderStream = clientOutputs.get(sender);
            PrintStream recipientStream = clientOutputs.get(recipient);

            //save to database
            String sql = "INSERT INTO private_chat_history (sender_id, receiver_id, message_text, sent_at) " +
                    "VALUES ((SELECT id FROM users WHERE user_name = ?), " +
                    "        (SELECT id FROM users WHERE user_name = ?), ?, NOW())";

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/lanchat", "root", "hehehehe");
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, sender);
                stmt.setString(2, recipient);
                stmt.setString(3, content);
                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }


            String formattedMsg = "PRIVATE:" + sender + ":" + recipient + ":" + content;

            if (senderStream != null) senderStream.println(formattedMsg);
            if (recipientStream != null && recipientStream != senderStream)
                recipientStream.println(formattedMsg);
        }
    }
}
