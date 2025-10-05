package server.net;



import server.messages.GroupMessage;
import server.messages.PrivateMessage;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
import static server.net.ChatServer.*;


public class ConvoServer {
    // Store connected clients
    public static final Map<String, PrintStream> clientOutputs = new HashMap<>();

    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        ChatServer svr = new ChatServer();
        try (ServerSocket server = new ServerSocket(888)) {
            System.out.println("Server started on port 888...");

            while (true) {
                Socket socket = server.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                // Handle each client in a new thread
                new Thread(() -> {
                    try {
                        handleClient(socket, svr);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, ChatServer svr) throws SQLException, ClassNotFoundException {
        try (
                PrintStream ps = new PrintStream(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String line;
            String username = null;

            while ((line = br.readLine()) != null) {
                System.out.println("Received: " + line);

                // Handle login
                if (line.startsWith("LOGIN:")) {
                    username = svr.login(line, ps);

                }

                //Handle signup
                if (line.startsWith("SIGNUP:")) {
                    username = svr.signup(line, ps);

                }

                else if (line.startsWith("FETCH_ONLINE")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String requester = parts[1];
                        synchronized (clientOutputs) {
                            PrintStream clientPs = clientOutputs.get(requester);
                            if (clientPs != null) {
                                String users = String.join(",", clientOutputs.keySet());
                                clientPs.println("ONLINE:" + users); // send online users to the requester only
                            }
                        }
                    }
                }

                if (line.startsWith("LOGOUT:")) {
                    svr.logout(line, ps);

                } else if (line.startsWith("MSG:")) {
                    if (username != null) {
                        // Format is "PRIVATE:<sender>:<recipient>:<message>"
                        String[] parts = line.split(":", 3);
                        if (parts.length >= 3) {
                            String sender = parts[1];
                            String msg = parts[2];

                            GroupMessage grp = new GroupMessage(sender, msg);
                            grp.send();
                        } else {
                            ps.println("⚠️ Invalid group message format.");
                        }
                    } else {
                        ps.println("⚠️ Please login first.");
                    }
                } else if (line.startsWith("PRIVATE:")) {
                    if (username != null) {
                        // Format is "PRIVATE:<sender>:<recipient>:<message>"
                        String[] parts = line.split(":", 4);
                        if (parts.length >= 4) {
                            String sender = parts[1];
                            String recipient = parts[2];
                            String msg = parts[3];

                            PrivateMessage prv = new PrivateMessage(sender, recipient, msg);
                            prv.send();
                        } else {
                            ps.println("⚠️ Invalid private message format.");
                        }
                    } else {
                        ps.println("⚠️ Please login first.");
                    }
                } else if (line.startsWith("FETCH_PRIVATE")) {
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String requester = parts[1];
                        String otherUser = parts[2];
                        svr.sendPrivateChatHistory(requester, otherUser);
                    }
                } else if (line.startsWith("FETCH_BROADCAST")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String requester = parts[1];
                        svr.sendBroadcastHistory(requester);
                    }
                }
            }
        } catch (java.net.SocketException e) {
            System.out.println("⚠️ Client disconnected abruptly: " + socket.getInetAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            removeClient(socket);
        }
    }
}


