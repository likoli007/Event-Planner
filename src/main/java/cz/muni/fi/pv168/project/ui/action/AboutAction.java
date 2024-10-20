package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AboutAction extends AbstractAction {
    private final JFrame parentFrame;

    public AboutAction(JFrame parentFrame){
        super("About");
        this.parentFrame = parentFrame;
        putValue(SHORT_DESCRIPTION, "About this application");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
                parentFrame,
                "TODO app\nVersion 0.0.1\nDeveloped by Team-11",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
