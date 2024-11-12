package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

public class LeftAlignedCellRenderer extends DefaultTableCellRenderer {
    public LeftAlignedCellRenderer() {
        setHorizontalAlignment(SwingConstants.LEFT);
    }
}
