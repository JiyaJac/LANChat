package Connection.Swing;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class ChatPage extends UITheme {

    private Connect connect;
    private String username;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private PrintStream ps;

    private JTabbedPane chatTabs;
    private Map<String, ChatPanel> privateChatTabs = new HashMap<>();

    public ChatPage(Connect connect, String username) {
        super("LAN Chat - " + username + "@" + connect, 900, 650);
        this.connect = connect;
        this.username = username;

        try {
            ps = new PrintStream(connect.getSocket().getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error setting up connection: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        setupUI();
        initializeChat();
        startMessageListener();

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

        // Tabs for chats
        chatTabs = new JTabbedPane();
        ChatPanel groupPanel = new ChatPanel(null); // null recipient = group chat
        chatTabs.addTab("Group Chat", groupPanel);

        userList.addListSelectionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null && !selectedUser.endsWith("(You)")) {
                openPrivateTab(selectedUser);
            }
        });

        mainContentPanel.add(usersPanel, BorderLayout.WEST);
        mainContentPanel.add(chatTabs, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initializeChat() {
        userListModel.clear();
        userListModel.addElement(username + " (You)");
        addMessageToGroup("System", "Welcome to the chat room, " + username + "!");
    }

    private void openPrivateTab(String user) {
        if (!privateChatTabs.containsKey(user)) {
            ChatPanel privatePanel = new ChatPanel(user);
            chatTabs.addTab(user, privatePanel);
            privateChatTabs.put(user, privatePanel);

            // Add close button to tab
            int index = chatTabs.indexOfComponent(privatePanel);
            chatTabs.setTabComponentAt(index, makeClosableTab(user, privatePanel));
        }
        chatTabs.setSelectedComponent(privateChatTabs.get(user));
        resetTabTitle(user);
    }

    private JPanel makeClosableTab(String title, Component tabContent) {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        JLabel label = new JLabel(title);
        JButton closeBtn = new JButton("x");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 10));
        closeBtn.setMargin(new Insets(0, 2, 0, 2));
        closeBtn.addActionListener(e -> {
            int i = chatTabs.indexOfComponent(tabContent);
            if (i >= 0) {
                chatTabs.remove(i);
                privateChatTabs.remove(title);
            }
        });

        tabPanel.add(label);
        tabPanel.add(closeBtn);
        return tabPanel;
    }

    public void startMessageListener() {
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
                            addMessageToGroup(sender, msg);
                        }
                    } else if (line.startsWith("PRIVATE:")) {
                        String[] parts = line.split(":", 4);
                        if (parts.length >= 4) {
                            String sender = parts[1];
                            String recipient = parts[2];
                            String msg = parts[3];

                            String tabKey = sender.equals(username) ? recipient : sender;

                            SwingUtilities.invokeLater(() -> {
                                openPrivateTab(tabKey);
                                privateChatTabs.get(tabKey).addMessage(sender, msg);

                                // Add notification (*) if tab is not active
                                if (chatTabs.getSelectedComponent() != privateChatTabs.get(tabKey)) {
                                    addNotification(tabKey);
                                }
                            });
                        }
                    }
                    else if (line.startsWith("PRIVATE_SYS:")) {
                        String[] parts = line.split(":", 3);
                        String sender = parts[1];   // The user who logged out
                        String msg = parts[2];      // "has logged out"

                        // Show in the private tab with that user if exists
                        SwingUtilities.invokeLater(() -> {
                            if (privateChatTabs.containsKey(sender)) {
                                privateChatTabs.get(sender).addMessage("System", msg);
                            }
                        });
                    }
                    else if (line.startsWith("ONLINE:")) {
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
                addMessageToGroup("System", "Disconnected from server.");
            }
        }).start();
    }

    private void addNotification(String user) {
        int index = chatTabs.indexOfComponent(privateChatTabs.get(user));
        if (index >= 0) {
            String title = chatTabs.getTitleAt(index);
            if (!title.endsWith("*")) {
                chatTabs.setTitleAt(index, title + "*");
            }
        }
    }

    private void resetTabTitle(String user) {
        int index = chatTabs.indexOfComponent(privateChatTabs.get(user));
        if (index >= 0) {
            chatTabs.setTitleAt(index, user);
        }
    }

    private void addMessageToGroup(String sender, String message) {
        ChatPanel groupPanel = (ChatPanel) chatTabs.getComponentAt(0);
        groupPanel.addMessage(sender, message);
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
                ps.flush();
            }
            if (userListModel != null) userListModel.removeElement(username);

            for (String u : privateChatTabs.keySet()) {
                privateChatTabs.get(u).addMessage("System", username + " has logged out.");
            }

            new ServerConnectionPage().setVisible(true);
            this.dispose();
        }
    }

    // Inner class for a single chat panel
    private class ChatPanel extends JPanel {
        private JTextArea chatArea;
        private JTextField messageField;
        private String recipient; // null = group chat

        public ChatPanel(String recipient) {
            super(new BorderLayout());
            this.recipient = recipient;

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            chatArea.setBackground(new Color(250, 250, 250));
            JScrollPane scrollPane = new JScrollPane(chatArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(scrollPane, BorderLayout.CENTER);

            JPanel messagePanel = new JPanel(new BorderLayout());
            messageField = new JTextField();
            messageField.addActionListener(e -> sendMessage());
            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(e -> sendMessage());
            messagePanel.add(messageField, BorderLayout.CENTER);
            messagePanel.add(sendButton, BorderLayout.EAST);

            add(messagePanel, BorderLayout.SOUTH);
        }

        public void sendMessage() {
            String msg = messageField.getText().trim();
            if (msg.isEmpty() || ps == null) return;

            if (recipient == null) {
                ps.println("MSG:" + username + ":" + msg);
            } else {
                ps.println("PRIVATE:" + username + ":" + recipient + ":" + msg);
            }
            messageField.setText("");
        }

        public void addMessage(String sender, String msg) {
            SwingUtilities.invokeLater(() -> {
                String timestamp = java.time.LocalTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
                );
                chatArea.append(String.format("[%s] %s: %s\n", timestamp, sender, msg));
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            });
        }
    }
}
