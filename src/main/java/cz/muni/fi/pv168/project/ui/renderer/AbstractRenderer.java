package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * The {@link AbstractRenderer} abstract class provides the ability of specifying custom rendering of certain type.
 *
 * @param <T> The type to be rendered
 * */
public abstract class AbstractRenderer<T> implements ListCellRenderer<T>, TableCellRenderer {

    private final Class<T> elementType;
    private final DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
    private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

    protected AbstractRenderer(Class<T> elementType) {
        this.elementType = elementType;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        // reset foreground color to default
        tableCellRenderer.setForeground(null);
        var label = (JLabel) tableCellRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        updateLabel(label, elementType.cast(value));
        return label;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends T> list, T value,
            int index, boolean isSelected, boolean cellHasFocus) {

        var label = (JLabel) listCellRenderer.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        updateLabel(label, value);
        return label;
    }

    protected abstract void updateLabel(JLabel label, T value);
}
