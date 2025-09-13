package net;

import database.DBConnection;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

public class ConvoServer {

    // Store connected clients
    private static final Map<String, PrintStream> clientOutputs = new HashMap<>();

    public static void main(String args[]) {
        try (ServerSocket server = new ServerSocket(888)) {
            System.out.println("Server started on port 888...");

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle each client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket s) {
        try (
                PrintStream ps = new PrintStream(s.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()))
        ) {
            String line;
            String username = null;

            while ((line = br.readLine()) != null) {
                System.out.println("Received: " + line);

                // Handle login
                if (line.startsWith("LOGIN:")) {
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        String user = parts[1];
                        String pass = parts[2];

                        DBConnection db = new DBConnection();
                        db.fetchUsers();

                        if (db.userCredentials.containsKey(user) &&
                                db.userCredentials.get(user).equals(pass)) {
                            ps.println("LOGIN_SUCCESS");   //  reply to client
                            username = user;
                            clientOutputs.put(username, ps);
                            System.out.println("✅ Login success for " + user);
                        } else {
                            ps.println("LOGIN_FAILED");    //  reply to client
                            System.out.println("❌ Login failed for " + user);
                        }
                    } else {
                        ps.println("LOGIN_FAILED");        // wrong format
                    }
                }

                // Handle chat messages
//                else if (line.startsWith("MSG:") && username != null) {
//                    String msg = line.substring(4); // message after "MSG:"
//                    broadcast(username + ": " + msg, username);
//                }
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            System.out.println("⚠️ Client disconnected: " + s.getInetAddress());
        } finally {
            try {
                s.close();
            } catch (IOException ignored) {}
        }
    }

    // Broadcast a message to all clients except sender
    private static void broadcast(String message, String sender) {
        for (Map.Entry<String, PrintStream> entry : clientOutputs.entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().println(message);
            }
        }
    }
}
