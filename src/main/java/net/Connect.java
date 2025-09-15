package net;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class Connect {
    private String host;
    private int port;
    private Socket socket;

    public Connect(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port); // actually tries to connect
    }

    public Socket getSocket() {
        return socket;
    }

//    public void communicate() throws IOException {
//        // to send data to the server
//        PrintStream ps = new PrintStream(socket.getOutputStream());
//
//        // to read data coming from the server
//        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        // to read data from the keyboard
//        BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));
//
//        String str, str1;
//        while (!(str = kb.readLine()).equals("exit")) {
//            ps.println(str);
//            str1 = br.readLine();
//            System.out.println(str1);
//        }
//
//        // close connection.
//        ps.close();
//        br.close();
//        kb.close();
//        socket.close();
//    }

//    public void close() {
//        try {
//            if (socket != null && !socket.isClosed()) {
//                socket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
