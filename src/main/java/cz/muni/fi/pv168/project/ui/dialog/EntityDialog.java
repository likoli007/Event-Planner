package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.ui.window.GenericToastWindow;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Optional;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

abstract class EntityDialog<E> {

    private final JPanel panel = new JPanel();

    EntityDialog() {
        panel.setLayout(new MigLayout("wrap 2"));
    }

    void add(String labelText, JComponent component) {
        var label = new JLabel(labelText);
        panel.add(label);
        panel.add(component, "wmin 250lp, grow");
    }

    abstract E getEntity();

    public Optional<E> show(JComponent parentComponent, String title) {
        int result = JOptionPane.showOptionDialog(parentComponent, panel, title,
                OK_CANCEL_OPTION, PLAIN_MESSAGE, null, null, null);
        if (result == OK_OPTION) {

            //TODO: the toastwindow should probably be a part of MainWindow that other classes communicate with?
            GenericToastWindow toastWindow = new GenericToastWindow("YOU CLICKED OK!", 150, 300);
            Thread object
                    = new Thread(toastWindow);
            object.start();

            return Optional.of(getEntity());
        } else {
            return Optional.empty();
        }
    }
}
