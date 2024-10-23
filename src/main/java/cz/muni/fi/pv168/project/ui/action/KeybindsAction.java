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
                        Ctrl+A - Select all entities from current table
                        Alt+F4 - Quit the app
                        ...
                        ...
                        """,
                "Important Keybinds",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
