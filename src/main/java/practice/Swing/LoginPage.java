package practice.Swing;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;


class LoginPage extends UITheme {

    private Connect connect;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage(Connect connect) {
        super("LAN Chat - Login",450,450);
        this.connect=connect;
        setupUI();
    }

    private void setupUI() {
        // Main panel with lighter blue background
        JPanel mainPanel = UIHelper.createPanel(new BorderLayout());
        mainPanel.setBackground(new Color(230, 245, 255)); // override theme if needed
        mainPanel.setBorder(new EmptyBorder(
                UITheme.PADDING * 2,
                UITheme.PADDING * 3,
                UITheme.PADDING * 2,
                UITheme.PADDING * 3
        ));

        JPanel contentPanel = UIHelper.createPanel(new FlowLayout());
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = UIHelper.createLabel("Login to Chat", true);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30)); // bigger & bolder
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Server info
        JLabel serverLabel = UIHelper.createLabel("Server: " + connect, false);
        serverLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        serverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(serverLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Username row
        JPanel usernameRow = UIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel userLabel = UIHelper.createLabel("Username:", false);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // bold & larger
        usernameField = UIHelper.createTextField(15);
        usernameRow.add(userLabel);
        usernameRow.add(usernameField);
        contentPanel.add(usernameRow);
        contentPanel.add(Box.createVerticalStrut(10));

        // Password row
        JPanel passwordRow = UIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel passLabel = UIHelper.createLabel("Password:", false);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // bold & larger
        passwordField = UIHelper.createPasswordField(15);
        passwordRow.add(passLabel);
        passwordRow.add(passwordField);
        contentPanel.add(passwordRow);
        contentPanel.add(Box.createVerticalStrut(25));

        // Button panel
        JPanel buttonPanel = UIHelper.createPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton signupButton = UIHelper.createButton("Sign Up");
        signupButton.setBackground(new Color(100, 180, 100));
        signupButton.addActionListener(e -> openSignup());

        JButton loginButton = UIHelper.createButton("Login");
        loginButton.addActionListener(e -> {
            try {
                attemptLogin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton backButton = UIHelper.createButton("Back");
        backButton.setBackground(new Color(150,150,150)); // red button
        backButton.setForeground(Color.WHITE); // white text
        backButton.addActionListener(e -> goBack());

        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Enter key triggers login
        passwordField.addActionListener(e -> {
            try {
                attemptLogin();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private void attemptLogin() throws SQLException, ClassNotFoundException {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Socket socket = connect.getSocket();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("LOGIN:" + user + ":" + pass);

            // ✅ Use readLine safely (with timeout option if needed)
            String response = in.readLine();

            if (response != null && response.startsWith("LOGIN_SUCCESS")) {
                ChatPage chatPage = new ChatPage(connect, user);
                chatPage.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        response != null ? response : "No response from server",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to server: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void goBack() {
        new ServerConnectionPage().setVisible(true);
        this.dispose();
    }
    private void openSignup() {
        new SignUpPage(connect).setVisible(true);
        this.dispose();
    }
}
