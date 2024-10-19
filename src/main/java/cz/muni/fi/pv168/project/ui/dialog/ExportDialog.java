package cz.muni.fi.pv168.project.ui.dialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ExportDialog{

    private JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    private FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");

    JPanel dialogPanel = new JPanel(new BorderLayout());
    JPanel exportPanel = new JPanel(new BorderLayout());
    JPanel exportOptionsPanel = new JPanel();
    JLabel filePathLabel = new JLabel("File Path:");
    JButton openButton = new JButton("Open");
    JTextField textField = new JTextField();
    JButton exportButton = new JButton("Export");
    JCheckBox eventsCheckBox = new JCheckBox("Events");
    JCheckBox categoriesCheckBox = new JCheckBox("Categories");
    JCheckBox templatesCheckBox = new JCheckBox("Templates");
    JCheckBox intervalsCheckBox = new JCheckBox("Intervals");

    private JDialog dialog;

    public ExportDialog(JFrame parentFrame) {
        dialog = new JDialog(parentFrame, "Export", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parentFrame);

        textField.setEditable(false);
        openButton.addActionListener(this::openButtonClicked);

        exportButton.addActionListener(this::exportButtonClicked);

        exportOptionsPanel.add(eventsCheckBox);
        exportOptionsPanel.add(categoriesCheckBox);
        exportOptionsPanel.add(templatesCheckBox);
        exportOptionsPanel.add(intervalsCheckBox);

        exportPanel.add(filePathLabel, BorderLayout.WEST);
        exportPanel.add(textField, BorderLayout.CENTER);
        exportPanel.add(openButton, BorderLayout.EAST);

        dialogPanel.add(exportOptionsPanel, BorderLayout.CENTER);
        dialogPanel.add(exportPanel, BorderLayout.NORTH);
        dialogPanel.add(exportButton, BorderLayout.SOUTH);

        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private void exportButtonClicked(ActionEvent e){
        //TODO: actual export logic
        dialog.dispose();
    }

    private void openButtonClicked(ActionEvent e){
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(dialog);
        if(result == JFileChooser.APPROVE_OPTION){
            //TODO: actual export logic, exceptions checking

            File file = fileChooser.getSelectedFile();
            textField.setText(file.getAbsolutePath());

        }else{
            textField.setText("");
        }
    }
}

