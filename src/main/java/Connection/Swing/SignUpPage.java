package Connection.Swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

class SignUpPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    //private Map<String, String> credentials; // shared from LoginPage

    public SignUpPage() {
        setupUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setTitle("LAN Chat - Sign Up");
        setResizable(false);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(20));

        // Username
        usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(250, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        content.add(usernameField);
        content.add(Box.createVerticalStrut(15));

        // Password
        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(250, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        content.add(passwordField);
        content.add(Box.createVerticalStrut(25));

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.addActionListener(e -> signup());
        content.add(signupBtn);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void signup() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

    }
}


