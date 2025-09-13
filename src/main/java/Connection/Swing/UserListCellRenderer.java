package Connection.Swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class UserListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setIcon(null);
        setBorder(new EmptyBorder(5, 10, 5, 10));

        if (value.toString().contains("(You)")) {
            setForeground(isSelected ? Color.WHITE : new Color(70, 130, 180));
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setForeground(isSelected ? Color.WHITE : Color.BLACK);
            setFont(getFont().deriveFont(Font.PLAIN));
        }

        return this;
    }
}

