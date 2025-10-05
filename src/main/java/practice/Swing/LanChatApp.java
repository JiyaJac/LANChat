package practice.Swing;



import javax.swing.*;


class LanChatApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Use default Swing look and feel
            new ServerConnectionPage().setVisible(true);
        });
    }
}

