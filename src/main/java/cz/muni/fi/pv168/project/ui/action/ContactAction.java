package cz.muni.fi.pv168.project.ui.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ContactAction extends AbstractAction {

    private final JFrame parentFrame;
    public ContactAction(JFrame parentFrame){
        super("Contact Us");
        this.parentFrame = parentFrame;
        putValue(SHORT_DESCRIPTION, "View our contact information");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
                parentFrame,
                """
                        Tereza Hrbková - XYZ
                        Šimon Brauner -  ABC
                        Alojz Holúbek - 瞎写的
                        Josef Žižka - 123
                        """,
                "Contact Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
