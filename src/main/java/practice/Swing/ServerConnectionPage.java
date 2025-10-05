package practice.Swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;


class ServerConnectionPage extends JFrame {

    private JTextField serverIPField;
    private JTextField serverPortField;

    public ServerConnectionPage() {
        setupUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setTitle("LAN Chat - Server DBConnection");
        setResizable(false);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 255));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Create content panel with vertical box layout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 245, 255));

        // Title
        JLabel titleLabel = new JLabel("LAN Chat");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 90, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Server DBConnection");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Server IP input label
        JLabel ipLabel = new JLabel("Enter Server IP Address:");
        ipLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(ipLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Server IP input field
        serverIPField = new JTextField("localhost");
        serverIPField.setFont(new Font("Arial", Font.PLAIN, 14));
        serverIPField.setMaximumSize(new Dimension(250, 35));
        serverIPField.setPreferredSize(new Dimension(250, 35));
        serverIPField.setAlignmentX(Component.CENTER_ALIGNMENT);
        serverIPField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 12, 10, 12)
        ));


        // Server port input label
        JLabel portLabel = new JLabel("Enter Server port:");
        portLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // Server port input field
        serverPortField = new JTextField("888");
        serverPortField.setFont(new Font("Arial", Font.PLAIN, 14));
        serverPortField.setMaximumSize(new Dimension(250, 35));
        serverPortField.setPreferredSize(new Dimension(250, 35));
        serverPortField.setAlignmentX(Component.CENTER_ALIGNMENT);
        serverPortField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        contentPanel.add(serverIPField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(portLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(serverPortField);
        contentPanel.add(Box.createVerticalStrut(25));

        // Connect button
        JButton connectButton = getJButton();
        contentPanel.add(connectButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Request focus for the IP and port field when window opens
        SwingUtilities.invokeLater(() -> {
            serverIPField.requestFocusInWindow();
            serverIPField.setCaretPosition(serverIPField.getText().length());
            serverPortField.requestFocusInWindow();
            serverPortField.setCaretPosition(serverPortField.getText().length());
        });
    }

    private JButton getJButton() {
        JButton connectButton = new JButton("Connect to Server");
        connectButton.setFont(new Font("Arial", Font.BOLD, 14));
        connectButton.setBackground(new Color(70, 130, 180));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.setBorder(new EmptyBorder(12, 25, 12, 25));
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.addActionListener(e -> {
            try {
                connectToServer();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        return connectButton;
    }

    private void connectToServer() throws IOException {
        String ip = serverIPField.getText().trim();
        String portS= serverPortField.getText().trim();
        if (ip.isEmpty() || portS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a server IP address", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int port = Integer.parseInt(portS);
        // Simulate connection attempt
        try {
            // Try to connect to server
            Connect connect = new Connect(ip, port); // Assuming this internally opens a Socket

            // If no exception → connection established
            JDialog connectDialog = new JDialog(this, "Connecting...", true);
            JLabel connectLabel = new JLabel("Connecting to server: " + ip + ":" + port);
            connectLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
            connectDialog.add(connectLabel);
            connectDialog.setSize(300, 120);
            connectDialog.setLocationRelativeTo(this);

            Timer timer = new Timer(1500, e -> {
                connectDialog.dispose();
                new LoginPage(connect).setVisible(true);
                this.dispose();
            });
            timer.setRepeats(false);
            timer.start();

            connectDialog.setVisible(true);

        } catch (IOException ex) {
            // If connection fails → show error, don’t open login page
            JOptionPane.showMessageDialog(
                    this,
                    "Could not connect to server at " + ip + ":" + port,
                    "DBConnection Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}