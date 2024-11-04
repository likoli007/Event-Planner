package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class RoundedLabel extends JLabel {
    private Color backgroundColor;
    private Color foregroundColor;
    private final int cornerRadius = 10;
    private final int padding = 5;

    public RoundedLabel(String text, Color backgroundColor){
        super(text);
        super.setBorder(new EmptyBorder(0, padding, 0, padding));

        this.backgroundColor = backgroundColor;
        this.foregroundColor = getContrastColor();

        setOpaque(false);
    }

    private Color getContrastColor(){
        var r = backgroundColor.getRed();
        var g = backgroundColor.getGreen();
        var b = backgroundColor.getBlue();

        var color =  ((r*299)+(g*587)+(b*114))/1000;

        return (color >= 128) ? Color.BLACK : Color.WHITE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.setColor(backgroundColor);

        g2d.fillRoundRect(0, 0 , getWidth(), getHeight(), cornerRadius, cornerRadius);

        setForeground(foregroundColor);
        super.paintComponent(g);
        g2d.dispose();
    }
}
