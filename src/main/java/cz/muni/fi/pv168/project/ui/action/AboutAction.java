package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.resources.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AboutAction extends AbstractAction {


    public AboutAction(){
        super("About");
        putValue(SHORT_DESCRIPTION, "About this application");
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
