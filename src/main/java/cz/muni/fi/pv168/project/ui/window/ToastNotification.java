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
        show(message, 2500);
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
        toastLabel.setForeground(Color.WHITE);
        toastLabel.setFont(new Font("Arial", Font.BOLD, 16));
        toastLabel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        toastWindow.add(toastLabel);
        toastWindow.pack();

        Rectangle parentBounds = parent.getBounds();
        int x = parentBounds.x + parentBounds.width - toastWindow.getWidth() - 20;
        int y = parentBounds.y + parentBounds.height - toastWindow.getHeight() - 20;
        toastWindow.setLocation(x, y);

        toastWindow.setVisible(true);

        new Timer(duration, e -> toastWindow.dispose()).start();
    }

    public void setParent(JFrame parent) {
        this.parent = parent;
    }
}
