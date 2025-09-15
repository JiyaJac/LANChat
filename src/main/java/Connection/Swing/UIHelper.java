package Connection.Swing;



import javax.swing.*;
import java.awt.*;

public class UIHelper {

    // Create a styled button
    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.BUTTON_FONT);
        btn.setBackground(UITheme.BUTTON_BG);
        btn.setForeground(UITheme.BUTTON_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return btn;
    }

    // Create a styled label (title or normal)
    public static JLabel createLabel(String text, boolean isTitle) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(isTitle ? UITheme.TITLE_FONT : UITheme.LABEL_FONT);
        lbl.setForeground(UITheme.TEXT);
        return lbl;
    }

    // Create a styled text field
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(UITheme.LABEL_FONT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    // Create a styled password field
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(UITheme.LABEL_FONT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return pf;
    }

    // Apply background to a frame
    public static void applyFrameStyle(JFrame frame) {
        frame.getContentPane().setBackground(UITheme.BACKGROUND);
    }

    // Create a panel with consistent background
    public static JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.BACKGROUND);
        return panel;
    }
}

