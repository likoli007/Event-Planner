package cz.muni.fi.pv168.project.ui.dialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImportDialog{

    private JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    private FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");

    JPanel dialogPanel = new JPanel(new BorderLayout());
    JPanel importPanel = new JPanel(new BorderLayout());

    JLabel filePathLabel = new JLabel("File Path:");
    JButton openButton = new JButton("Open");
    JTextField textField = new JTextField();
    JTextArea fileInfoTextArea = new JTextArea();
    JButton importButton = new JButton("Import");

    JDialog dialog = new JDialog((JFrame) null, "Import", true);

    public ImportDialog() {

        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);

        textField.setEditable(false);
        fileInfoTextArea.setEditable(false);
        fileInfoTextArea.setBackground(null);

        openButton.addActionListener(this::openButtonClicked);

        importButton.addActionListener(this::importButtonClicked);

        importPanel.add(filePathLabel, BorderLayout.WEST);
        importPanel.add(textField, BorderLayout.CENTER);
        importPanel.add(openButton, BorderLayout.EAST);

        dialogPanel.add(fileInfoTextArea, BorderLayout.CENTER);
        dialogPanel.add(importPanel, BorderLayout.NORTH);
        dialogPanel.add(importButton, BorderLayout.SOUTH);

        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private void importButtonClicked(ActionEvent e){
        //TODO: actual import logic
        dialog.dispose();
    }

    private void openButtonClicked(ActionEvent e){
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(dialogPanel);
        if(result == JFileChooser.APPROVE_OPTION){
            //TODO: actual import logic, exceptions checking
            // actual import logic should be done only after the user selects the 'import' button

            File file = fileChooser.getSelectedFile();
            textField.setText(file.getAbsolutePath());

            fileInfoTextArea.setText("""
                            Total No. of events: 10
                            Total No. of categories: 9
                            Total No. of templates: 8
                            Total No. of intervals: 7
                            """);

        }else{
            textField.setText("");
        }
    }

}
