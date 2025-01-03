package cz.muni.fi.pv168.project.ui.dialog;

import javax.swing.*;

public class SuccessDialog {
    public static void show(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
