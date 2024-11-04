package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeRenderer extends AbstractRenderer<LocalTime> {
    public LocalTimeRenderer() {
        super(LocalTime.class);
    }

    @Override
    protected void updateLabel(JLabel label, LocalTime value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedDateTime = value.format(formatter);
        label.setText(formattedDateTime);
    }

}
