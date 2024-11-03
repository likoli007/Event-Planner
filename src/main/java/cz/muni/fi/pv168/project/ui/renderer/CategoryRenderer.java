package cz.muni.fi.pv168.project.ui.renderer;

import cz.muni.fi.pv168.project.model.Category;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CategoryRenderer extends JPanel implements TableCellRenderer {
    public CategoryRenderer(){
        //why 2??? -> TODO: calculate this number somehow
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        removeAll();

        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }

        if (value instanceof Category) {
            CategoryColorRectangle categoryColorRectangle = new CategoryColorRectangle( ((Category) value).getColor());
            add(categoryColorRectangle);
            add( new JLabel(((Category) value).getName()));
        }

        revalidate();
        repaint();
        return this;
    }

}
