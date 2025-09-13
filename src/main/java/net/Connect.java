package net;



import java.io.*;
import java.net.*;


public class Connect {
    String host;
    int port;

    public Connect(String host, int port ) throws IOException {
        this.host=host;
        this.port=port;
        Socket s = new Socket(host, port);
    }

    public void communicate(Socket s) throws IOException {
        // to send data to the server
        PrintStream ps = new PrintStream(s.getOutputStream());

        // to read data coming from the server
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        // to read data from the keyboard
        BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));

        String str, str1;
        // repeat as long as exit
        // is not typed at client
        while (!(str = kb.readLine()).equals("exit")) {
            // send to the server
            ps.println(str + "\n");
            // receive from the server
            str1 = br.readLine();
            System.out.println(str1);
        }

        // close connection.
        ps.close();
        br.close();
        kb.close();
        s.close();
    }
}
