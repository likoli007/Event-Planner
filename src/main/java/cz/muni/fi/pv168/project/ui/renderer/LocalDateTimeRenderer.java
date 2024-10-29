package cz.muni.fi.pv168.project.ui.renderer;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeRenderer extends AbstractRenderer<LocalDateTime> {
    public LocalDateTimeRenderer() {
        super(LocalDateTime.class);
    }

    @Override
    protected void updateLabel(JLabel label, LocalDateTime value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm EEE dd.MM.yyyy");
        String formattedDateTime = value.format(formatter);
        label.setText(formattedDateTime);
    }

}
