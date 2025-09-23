package server.messages;


import java.io.PrintStream;

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

            String formattedMsg = "PRIVATE:" + sender + ":" + recipient + ":" + content;

            if (senderStream != null) senderStream.println(formattedMsg);
            if (recipientStream != null && recipientStream != senderStream)
                recipientStream.println(formattedMsg);
        }
    }
}
