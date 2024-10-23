package cz.muni.fi.pv168.project;

import com.formdev.flatlaf.FlatLightLaf;
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
        initFlatLafLookAndFeel();
        EventQueue.invokeLater(() -> new MainWindow().show());
    }

    private static void initFlatLafLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Layout initialization failed", ex);
        }
    }
}
