package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.*;
import java.awt.*;

public class CategoryColorRectangle extends JPanel {
    private Color categoryColor;

    public CategoryColorRectangle(Color color){
        this.categoryColor = color;

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.setColor(categoryColor);

        g2d.fillRect(0,0,10,10);


        //g.drawString(getText(), 15, g.getFontMetrics().getAscent());
        //super.paintComponent(g);
        g2d.dispose();
    }

}
