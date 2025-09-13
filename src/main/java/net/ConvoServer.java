package net;


import java.io.*;
import java.net.*;

public class ConvoServer {
    public static void main(String args[])
            throws Exception
    {
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

    // Function to handle a single client
    private static void handleClient(Socket s) {
        try (
                //to send data to the client
                PrintStream ps = new PrintStream(s.getOutputStream());

                //to read data from the client
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

                // to read data from the keyboard
                BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
        ) {
            while (true) {
                String str, str1;

                // repeat as long as the client
                // does not send a null string
                // read from client
                while ((str = br.readLine()) != null) {
                    System.out.println(str);
                    str1 = kb.readLine();
                    // send to client
                    ps.println(str1);
                }

            }
        } catch (IOException e) {
            System.out.println("⚠️ Client disconnected: " + s.getInetAddress());
        } finally {
            try {
                s.close();
            } catch (IOException ignored) {}
        }
    }
}


//
//        // server executes continuously
//        while (true) {
//            String str, str1;
//
//            // repeat as long as the client
//            // does not send a null string
//            // read from client
//            while ((str = br.readLine()) != null) {
//                System.out.println(str);
//                str1 = kb.readLine();
//                // send to client
//                ps.println(str1);
//            }
//
//            // close connection
//            ps.close();
//            br.close();
//            kb.close();
//            ss.close();
//            s.close();
//
//            // terminate application
//            System.exit(0);
//
//        } // end of while
//    }
//}

