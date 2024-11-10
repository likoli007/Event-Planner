package cz.muni.fi.pv168.project.ui.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeybindsAction extends AbstractAction {

    private final JFrame parentFrame;
    public KeybindsAction(JFrame parentFrame){
        super("Key Shortcuts");
        this.parentFrame = parentFrame;

        putValue(SHORT_DESCRIPTION, "View the different key shortcuts");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
                parentFrame,
                """
                        Ctrl + A - Select all items in the current table
                        Ctrl + N - Add a new item to the current table
                        Ctrl + E - Edit the selected item
                        Ctrl + D - Delete the selected item
                        Ctrl + Q - Quit the app
                        Alt + I - Import data
                        Alt + E - Export data
                        """,
                "Important Keybinds",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
