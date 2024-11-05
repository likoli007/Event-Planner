package cz.muni.fi.pv168.project.ui.window;

import javax.swing.*;
import java.awt.*;

class ToastPanel extends JPanel implements Runnable {
    String message;
    int opacity = 255;

    public ToastPanel(String text){
        message = text;
        int width = getFontMetrics(getFont()).stringWidth(message) + 40;
        int height = getFontMetrics(getFont()).getHeight() + 40;
        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setMaximumSize(new Dimension(width, height));

    }

    public void paintComponent(Graphics g){
        //super.paintComponent(g);

        int rectWidth = g.getFontMetrics().stringWidth(message) + 20;
        int rectHeight = g.getFontMetrics().getHeight() + 10;

        int x = (getWidth() - rectWidth) / 2;
        int y = (getHeight() - rectHeight) / 2;

        g.setColor(new Color(32, 32, 32, opacity));
        g.fillRoundRect(x, y, rectWidth, rectHeight, 20, 20);

        g.setColor(new Color(255, 255, 255, opacity));
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (rectWidth - metrics.stringWidth(message)) / 2;
        int textY = y + ((rectHeight - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(message, textX, textY);
    }

    @Override
    public void run() {
        try {
            System.out.println("PAINTING " + message);
            Thread.sleep(800);
            for (int i = 255; i > 20; i -= 30){
                opacity = i;
                repaint();
                Thread.sleep(100);
            }
            SwingUtilities.invokeLater(() -> {
                Container parent = getParent();
                if (parent != null) {
                    parent.remove(ToastPanel.this);
                    parent.revalidate();
                    parent.repaint();
                }
                System.out.println("DELETED " + message);
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ToastWindowContainer extends JPanel {
    public ToastWindowContainer(){
        setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder());
    }

    public void addToast(String text){
        ToastPanel toast = new ToastPanel(text);
        add(toast);
        revalidate();
        repaint();
        Thread thread = new Thread(toast);
        thread.start();
    }


}
