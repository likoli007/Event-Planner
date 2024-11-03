package cz.muni.fi.pv168.project.ui.window;


import javax.swing.*;
import java.awt.*;

public class GenericToastWindow extends JFrame implements Runnable{
    String message;

    JWindow window;

    @Override
    public void run() {
        showToast();
    }

    class ToastPanel extends JPanel{
        public void paintComponent(Graphics g){

            int width = g.getFontMetrics().stringWidth(message);
            int height = g.getFontMetrics().getHeight();

            g.setColor(Color.black);
            g.fillRect(10, 10, width + 30, height + 30);

            g.setColor(new Color(255,255,255,240));
            g.drawString(message, 25, 27);

        }
    }
    public GenericToastWindow(String text, int x, int y){
        message = text;

        window = new JWindow();

        window.setBackground(new Color(0,0,0,0) );

        ToastPanel panel = new ToastPanel();

        window.add(panel);
        window.setLocation(x, y);
        window.setSize(300, 100);
    }


    public void showToast() {
        try {
            window.setOpacity(1);
            window.setVisible(true);

            Thread.sleep(2000);

            for (double opacity = 1.0; opacity > 0.2; opacity -= 0.1){
                Thread.sleep(100);
                window.setOpacity((float) opacity);
            }
            window.setVisible(false);
        }
        catch (Exception e){
            //TODO: actual error handling
            System.out.println(e.getMessage());
        }

    }



}



