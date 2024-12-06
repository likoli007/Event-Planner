package cz.muni.fi.pv168.project.ui.window;

import javax.swing.*;
import java.awt.*;

public class ToastNotification {
    private static ToastNotification instance;
    private JFrame parent;
    private ToastNotification() {
    }

    public static ToastNotification getInstance() {
        if (instance == null) {
            instance = new ToastNotification();
        }
        return instance;
    }

    public void show( String message) {
        show(message, 2000);
    }
    
    public void show( String message, int duration) {
        if (parent == null) {
            throw new IllegalStateException("Parent JFrame must not be null.");
        }

        // Create a JWindow (borderless and undecorated)
        JWindow toastWindow = new JWindow(parent);
        toastWindow.setAlwaysOnTop(true);

        // Create a label to display the message
        JLabel toastLabel = new JLabel(message);
        toastLabel.setOpaque(true);
        toastLabel.setBackground(new Color(0, 0, 0, 170)); // Transparent black background
        toastLabel.setForeground(Color.WHITE); // White text
        toastLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font size
        toastLabel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Larger padding

        // Add the label to the window
        toastWindow.add(toastLabel);
        toastWindow.pack();

        // Get the position of the parent window
        Rectangle parentBounds = parent.getBounds();
        int x = parentBounds.x + parentBounds.width - toastWindow.getWidth() - 20; // Right corner within parent
        int y = parentBounds.y + parentBounds.height - toastWindow.getHeight() - 20; // Bottom corner within parent
        toastWindow.setLocation(x, y);

        // Show the toast
        toastWindow.setVisible(true);

        // Close the toast after the specified duration
        new Timer(duration, e -> toastWindow.dispose()).start();
    }

    public void setParent(JFrame parent) {
        this.parent = parent;
    }
}
