package server.messages;

import server.messages.Message;
import static server.net.ConvoServer.clientOutputs;

import java.io.PrintStream;
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
        synchronized (clientOutputs) {
            for (Map.Entry<String, PrintStream> entry : clientOutputs.entrySet()) {
                entry.getValue().println("MSG:" + sender + ":" + content);
            }
        }
    }
}
