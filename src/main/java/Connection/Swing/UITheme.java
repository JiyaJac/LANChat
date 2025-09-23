package Connection.Swing;





import javax.swing.*;
import java.awt.*;


public class UITheme extends JFrame {

    public UITheme(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }
    // Colors
    public static final Color BACKGROUND = new Color(225, 240, 255); // light blue
    public static final Color PRIMARY = new Color(25, 70, 150); // dark blue
    public static final Color BUTTON_BG = new Color(0, 120, 215); // bright blue
    public static final Color BUTTON_TEXT = Color.WHITE;
    public static final Color TEXT = Color.BLACK;

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Padding
    public static final int PADDING = 12;
}



