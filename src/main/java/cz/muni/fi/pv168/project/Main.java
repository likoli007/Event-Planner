package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.ui.MainWindow;

import javax.swing.UIManager;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The entry point of the application.
 */
public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainWindow().show());
    }
}
