package cz.muni.fi.pv168.project.ui.utils;

import javax.swing.*;

public class NumericInputValue {
    public static int get(String input, String fieldName) {
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No " + fieldName + " set, defaulting to 0",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return 0;
        } else {
            return Integer.parseInt(input);
        }
    }
}
