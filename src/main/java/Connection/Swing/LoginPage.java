package Connection.Swing;

import database.DBConnection;
import net.Connect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private String serverIP;
    //private Map<String, String> credentials;

    public LoginPage(String serverIP) {
        this.serverIP = serverIP;
        //initializeCredentials();
        setupUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 450);
        setLocationRelativeTo(null);
        setTitle("LAN Chat - Login");
        setResizable(false);
    }

//    private void initializeCredentials() {
//        credentials = new HashMap<>();
//        credentials.put("admin", "admin123");
//        credentials.put("user1", "pass123");
//        credentials.put("user2", "pass456");
//        credentials.put("john", "john123");
//        credentials.put("jane", "jane456");
//    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 250, 255));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Content panel with vertical layout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 250, 255));

        // Title
        JLabel titleLabel = new JLabel("Login to Chat");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 90, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Server info
        JLabel serverLabel = new JLabel("Server: " + serverIP);
        serverLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        serverLabel.setForeground(new Color(100, 100, 100));
        serverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(serverLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Form panel with username and password
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(245, 250, 255));

        // Username row
        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        usernameRow.setBackground(new Color(245, 250, 255));
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setPreferredSize(new Dimension(80, 25));

        usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(200, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        usernameRow.add(userLabel);
        usernameRow.add(usernameField);
        formPanel.add(usernameRow);
        formPanel.add(Box.createVerticalStrut(10));

        // Password row
        JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        passwordRow.setBackground(new Color(245, 250, 255));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setPreferredSize(new Dimension(80, 25));

        passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)
        ));

        passwordRow.add(passLabel);
        passwordRow.add(passwordField);
        formPanel.add(passwordRow);

        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 250, 255));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(10, 25, 10, 25));
        loginButton.addActionListener(e -> {
            try {
                attemptLogin();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBackground(new Color(150, 150, 150));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        backButton.addActionListener(e -> goBack());

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Demo credentials info
        JLabel demoLabel = new JLabel("<html><center><u>Demo Credentials:</u><br>"
                + "admin/admin123<br>"
                + "user1/pass123<br>"
                + "user2/pass456</center></html>", JLabel.CENTER);
        demoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        demoLabel.setForeground(new Color(100, 100, 100));
        demoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(demoLabel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Enter key support
        passwordField.addActionListener(e -> {
            try {
                attemptLogin();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Request focus for username field when window opens
        SwingUtilities.invokeLater(() -> {
            usernameField.requestFocusInWindow();
        });
    }

    private void attemptLogin() throws SQLException, ClassNotFoundException {

        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connect connect = new Connect(serverIP, 888);
            Socket socket = connect.getSocket();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("LOGIN:" + user + ":" + pass);
            String response = in.readLine();

            if ("LOGIN_SUCCESS".equals(response)) {
                new ChatPage(serverIP, user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

            connect.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void goBack() {
        new ServerConnectionPage().setVisible(true);
        this.dispose();
    }
}
