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
                        Tereza Hrbková - 492946@mail.muni.cz
                        Šimon Brauner - 525160@mail.muni.cz
                        Alojz Holúbek - 514416@mail.muni.cz
                        Josef Žižka - jzizka@mail.muni.cz
                        """,
                "Contact Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
