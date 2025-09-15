package Connection.Swing;

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
import java.util.Map;

class SignUpPage extends JFrame {
    private Connect connect;
    private JTextField usernameField;
    private JPasswordField passwordField;
    //private Map<String, String> credentials; // shared from LoginPage


    public SignUpPage(Connect connect) {
        this.connect=connect;
        setupUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setTitle("LAN Chat - Sign Up");
        setResizable(false);
        setVisible(true);
    }

    private void setupUI() {
        // Main panel with light blue background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(230, 245, 255));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(30));

        // Username
        JPanel usernameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        usernameRow.setOpaque(false);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        usernameField = new JTextField(15);
        usernameRow.add(userLabel);
        usernameRow.add(usernameField);
        content.add(usernameRow);
        content.add(Box.createVerticalStrut(15));

        // Password
        JPanel passwordRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        passwordRow.setOpaque(false);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordField = new JPasswordField(15);
        passwordRow.add(passLabel);
        passwordRow.add(passwordField);
        content.add(passwordRow);
        content.add(Box.createVerticalStrut(25));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBackground(new Color(0, 120, 215)); // Blue
        signupBtn.setForeground(Color.WHITE);
        signupBtn.addActionListener(e -> {
            try {
                attemptSignup();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(150, 150, 150));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> goBack());

        buttonPanel.add(signupBtn);
        buttonPanel.add(backBtn);

        content.add(buttonPanel);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);

        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private void attemptSignup() throws SQLException, ClassNotFoundException {

        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Socket socket = connect.getSocket();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("SIGNUP:" + user + ":" + pass);
            String response = in.readLine();

            if ("SIGNUP_SUCCESS".equals(response)) {

                new ChatPage(connect, user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "username already exists", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void goBack() {
        new LoginPage(connect).setVisible(true);
        this.dispose();
    }
}


