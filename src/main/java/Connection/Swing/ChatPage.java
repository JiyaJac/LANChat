package Connection.Swing;

import net.Connect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class ChatPage extends JFrame {

    private Connect connect;
    private String username;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintStream ps;

    public ChatPage(Connect connect, String username) {
        this.connect = connect;
        this.username = username;

        try {
            // Create PrintStream for sending messages
            ps = new PrintStream(connect.getSocket().getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error setting up connection: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        setupUI();
        initializeChat();
        startMessageListener();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setTitle("LAN Chat - " + username + " @ " + this.connect);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 90, 150));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Left side of header
        JPanel leftHeaderPanel = new JPanel();
        leftHeaderPanel.setLayout(new BoxLayout(leftHeaderPanel, BoxLayout.Y_AXIS));
        leftHeaderPanel.setBackground(new Color(60, 90, 150));

        JLabel chatTitle = new JLabel("LAN Chat Room - Connected as: " + username);
        chatTitle.setFont(new Font("Arial", Font.BOLD, 16));
        chatTitle.setForeground(Color.WHITE);
        chatTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel serverLabel = new JLabel("Server: " + connect);
        serverLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        serverLabel.setForeground(new Color(220, 220, 220));
        serverLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftHeaderPanel.add(chatTitle);
        leftHeaderPanel.add(serverLabel);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(180, 70, 70));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Users panel
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBackground(new Color(248, 250, 255));
        usersPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Online Users",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                new Color(60, 90, 150)
        ));
        usersPanel.setPreferredSize(new Dimension(220, 0));

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("Arial", Font.PLAIN, 13));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(200, 0));
        usersPanel.add(userScrollPane, BorderLayout.CENTER);

        // Chat panel
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Chat Messages",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                new Color(60, 90, 150)
        ));

        // Chat area
        chatArea = new JTextArea();
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(250, 250, 250));

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Message input panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Send Message",
                0, 0,
                new Font("Arial", Font.BOLD, 11),
                new Color(60, 90, 150)
        ));

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 13));
        messageField.setPreferredSize(new Dimension(400, 35));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        messageField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        sendButton.addActionListener(e -> sendMessage());

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        // Add panels to main content
        mainContentPanel.add(usersPanel, BorderLayout.WEST);
        mainContentPanel.add(chatPanel, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initializeChat() {
        // Clear and add self to user list
        userListModel.clear();
        userListModel.addElement(username + " (You)");

        // Welcome text
        chatArea.setText("=== LAN Chat Room ===\n");
        chatArea.append("Server: " + connect + "\n");
        chatArea.append("Connected as: " + username + "\n");
        chatArea.append("========================\n\n");

        addMessage("System", "Welcome to the chat room, " + username + "!");
        messageField.requestFocus();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && ps != null) {
            ps.println("MSG:" + username + ":" + message); // send to server
            messageField.setText("");
        }
    }

    public void addMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            );

            String formattedMessage;
            if (sender.equals("System")) {
                formattedMessage = String.format("[%s] *** %s ***\n", timestamp, message);
            } else {
                formattedMessage = String.format("[%s] %s: %s\n", timestamp, sender, message);
            }

            chatArea.append(formattedMessage);
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connect.getSocket().getInputStream())
                );
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("MSG:")) {
                        String[] parts = line.split(":", 3);
                        if (parts.length >= 3) {
                            String sender = parts[1];
                            String msg = parts[2];
                            addMessage(sender, msg);
                        }
                    } else if (line.startsWith("ONLINE:")) {
                        // update online users
                        String[] users = line.substring(7).split(",");
                        SwingUtilities.invokeLater(() -> {
                            userListModel.clear();
                            for (String u : users) {
                                if (u.equals(username)) {
                                    userListModel.addElement(u + " (You)");
                                } else {
                                    userListModel.addElement(u);
                                }
                            }
                        });
                    }
                }
            } catch (IOException e) {
                addMessage("System", "Disconnected from server.");
            }
        }).start();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout and return to server selection?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (ps != null) {
                ps.println("LOGOUT:" + username);
            }
            new ServerConnectionPage().setVisible(true);
            this.dispose();
        }
    }
}
