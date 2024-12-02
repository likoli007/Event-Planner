package cz.muni.fi.pv168.project.ui.dialog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.pv168.project.model.DuplicateType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

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

    private boolean importAllowed = false;

    private DuplicateType duplicateHandling = DuplicateType.UNDEFINED;

    JLabel overwriteOptionsLabel = new JLabel("Default overwrite behaviour:");
    JRadioButton noneRadioButton = new JRadioButton("Ask for each");
    JRadioButton overwriteRadio = new JRadioButton("Overwrite");
    JRadioButton skipRadio = new JRadioButton("Skip");
    JRadioButton duplicateRadio = new JRadioButton("Duplicate");
    ButtonGroup overwriteOptionsGroup = new ButtonGroup();
    JPanel overwriteOptionsPanel = new JPanel();
    JPanel overwriteOptionsButtonPanel = new JPanel();

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

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(overwriteOptionsLabel);
        mainPanel.add(labelPanel);
        //overwriteOptionsPanel.setLayout(new BoxLayout(overwriteOptionsPanel, BoxLayout.Y_AXIS));
        overwriteOptionsButtonPanel.setLayout(new FlowLayout());

        noneRadioButton.addActionListener(e -> duplicateHandling = DuplicateType.UNDEFINED);
        overwriteRadio.addActionListener(e -> duplicateHandling = DuplicateType.OVERWRITE);
        skipRadio.addActionListener(e -> duplicateHandling = DuplicateType.CANCEL);
        duplicateRadio.addActionListener(e -> duplicateHandling = DuplicateType.DUPLICATE);
        overwriteOptionsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        overwriteOptionsGroup.add(noneRadioButton);
        overwriteOptionsGroup.add(overwriteRadio);
        overwriteOptionsGroup.add(skipRadio);
        overwriteOptionsGroup.add(duplicateRadio);
        noneRadioButton.setSelected(true);

        overwriteOptionsButtonPanel.add(noneRadioButton);
        overwriteOptionsButtonPanel.add(overwriteRadio);
        overwriteOptionsButtonPanel.add(skipRadio);
        overwriteOptionsButtonPanel.add(duplicateRadio);

        //overwriteOptionsPanel.add(overwriteOptionsButtonPanel);


        mainPanel.add(overwriteOptionsButtonPanel);
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
        if (resultFilePath.isEmpty()){
            JOptionPane.showMessageDialog(dialog, "Error: file path cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        importAllowed = true;
        dialog.dispose();
    }

    public DuplicateType getDuplicateHandling(){
        return duplicateHandling;
    }

    public String getResultFilePath(){
        return resultFilePath;
    }

    public boolean canImport(){
        return importAllowed;
    }

    private int getArrayLength(JsonNode rootNode, String fieldName) {
        JsonNode arrayNode = rootNode.get(fieldName);
        if (arrayNode != null && arrayNode.isArray()) {
            return arrayNode.size();
        }
        return 0;
    }

    private void getJSONStatistics(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(file);

        fileInfoTextArea.setText(
                "Events: " + getArrayLength(rootNode, "events") + "\n" +
                "Categories: " + getArrayLength(rootNode, "categories") + "\n" +
                "Templates: " + getArrayLength(rootNode, "templates") + "\n" +
                "Intervals: " + getArrayLength(rootNode, "timeUnits") + "\n"
        );
    }

    private void openButtonClicked(ActionEvent e){
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(mainPanel);
        if(result == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            textField.setText(file.getAbsolutePath());
            try {
                getJSONStatistics(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "An error occured while importing!\n" +
                                "This was likely caused by basic JSON syntax fault.\n" +
                                "Please ensure the JSON file is intact",
                        "Alert", JOptionPane.ERROR_MESSAGE);
                dialog.dispose();
            }
        }else{
            textField.setText("");
        }
        dialog.pack();
    }
}
