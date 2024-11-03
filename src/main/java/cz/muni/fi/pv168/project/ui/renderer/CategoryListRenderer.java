package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.pv168.project.model.Category;


public class CategoryListRenderer extends JPanel implements TableCellRenderer {

    public CategoryListRenderer(){
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        removeAll();

        if (value instanceof List<?>) {
            List<?> categories = (List<?>) value;

            for (Object obj : categories) {
                if (obj instanceof Category) {
                    Category category = (Category) obj;

                    //just cyan for now
                    RoundedLabel roundedLabel = new RoundedLabel(category.getName(), category.getColor());
                    roundedLabel.setForeground(Color.BLACK);
                    add(roundedLabel);
                }
            }
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        revalidate();
        repaint();
        return this;
    }
}
