package cz.muni.fi.pv168.project.ui.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ImportDialog{

    private JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    private FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");

    JPanel mainPanel = new JPanel();

    JPanel importPanel = new JPanel();

    JLabel filePathLabel = new JLabel("File Path:");
    JButton openButton = new JButton("Open");
    JTextField textField = new JTextField();
    JTextArea fileInfoTextArea = new JTextArea();
    JButton importButton = new JButton("Import");

    JPanel buttonPanel = new JPanel();

    private JDialog dialog;

    private String resultFilePath;

    public ImportDialog(JFrame parentFrame) {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        importPanel.setLayout(new FlowLayout());

        dialog = new JDialog(parentFrame, "Import", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);

        textField.setEditable(true);
        textField.setSize(200, 20);
        textField.setPreferredSize(new Dimension(200, 20));
        fileInfoTextArea.setEditable(false);
        fileInfoTextArea.setBackground(null);

        openButton.addActionListener(this::openButtonClicked);

        importButton.addActionListener(this::importButtonClicked);

        importPanel.add(filePathLabel, BorderLayout.WEST);
        importPanel.add(textField, BorderLayout.CENTER);
        importPanel.add(openButton, BorderLayout.EAST);

        mainPanel.add(importPanel);
        mainPanel.add(fileInfoTextArea);

        //mainPanel.add(importButton);

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(importButton);
        mainPanel.add(buttonPanel);

        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void importButtonClicked(ActionEvent e){
        resultFilePath = textField.getText();
        dialog.dispose();
    }

    public String getResultFilePath(){
        return resultFilePath;
    }

    private void openButtonClicked(ActionEvent e){
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(mainPanel);
        if(result == JFileChooser.APPROVE_OPTION){
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
        dialog.pack();
    }

}
