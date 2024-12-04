package cz.muni.fi.pv168.project.ui.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ExportDialog {
    private final JFileChooser fileChooser;
    private final FileNameExtensionFilter filter;
    private final JPanel mainPanel;
    private final JPanel exportPanel;
    private final JPanel exportOptionsPanel;
    private final JLabel filePathLabel;
    private final JButton openButton;
    private final JTextField textField;
    private final JButton exportButton;
    private final JRadioButton filterRadioButton;
    private final JRadioButton noFilterRadioButton;
    private final ButtonGroup filterGroup;
    private final JLabel exportStatisticsLabel;
    private final JPanel radioPanel;
    private final JPanel buttonPanel;

    private String resultFilePath;
    private boolean exportFiltered;

    private final JDialog dialog;

    private int allEventsCount;
    private int filteredEventsCount;

    boolean exportAllowed = false;

    public ExportDialog(JFrame parentFrame, int allEventsCount, int filteredEventsCount) {
        this.fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        this.filter = new FileNameExtensionFilter("JSON Files", "json");
        this.allEventsCount = allEventsCount;
        this.filteredEventsCount = filteredEventsCount;


        this.mainPanel = new JPanel();
        this.exportPanel = new JPanel(new FlowLayout());
        this.exportOptionsPanel = new JPanel(new BorderLayout());
        this.filePathLabel = new JLabel("File Path:");
        this.openButton = new JButton("Open");
        this.textField = new JTextField();
        this.exportButton = new JButton("Export");
        this.filterRadioButton = new JRadioButton("Export filtered");
        this.noFilterRadioButton = new JRadioButton("Export all");
        this.filterGroup = new ButtonGroup();
        this.exportStatisticsLabel = new JLabel();
        this.radioPanel = new JPanel();
        this.buttonPanel = new JPanel();

        this.dialog = new JDialog(parentFrame, "Export", true);
        this.dialog.setSize(300, 150);
        this.dialog.setLocationRelativeTo(parentFrame);
        this.dialog.setResizable(false);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        exportButton.addActionListener(this::exportButtonClicked);

        textField.setEditable(true);
        textField.setSize(200, 20);
        textField.setPreferredSize(new Dimension(200, 20));
        openButton.addActionListener(this::openButtonClicked);

        exportPanel.add(filePathLabel);
        exportPanel.add(textField);
        exportPanel.add(openButton);

        filterGroup.add(noFilterRadioButton);
        filterGroup.add(filterRadioButton);

        JLabel filterLabel = new JLabel("Events export options:");
        filterLabel.setBorder(new EmptyBorder(10, 5, 5, 0));
        exportStatisticsLabel.setBorder(new EmptyBorder(0, 5, 5, 0));
        exportStatisticsLabel.setVisible(true);
        exportStatisticsLabel.setText("Will export " + allEventsCount +" events.");

        filterRadioButton.addActionListener(this::filterRadioButtonClicked);
        noFilterRadioButton.addActionListener(this::noFilterRadioButtonClicked);
        noFilterRadioButton.setSelected(true);
        radioPanel.add(noFilterRadioButton);
        radioPanel.add(filterRadioButton);

        exportOptionsPanel.add(filterLabel, BorderLayout.NORTH);
        exportOptionsPanel.add(radioPanel, BorderLayout.CENTER);
        exportOptionsPanel.add(exportStatisticsLabel, BorderLayout.SOUTH);

        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(exportButton);

        mainPanel.add(exportPanel);
        mainPanel.add(exportOptionsPanel);

        mainPanel.add(buttonPanel);

        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        dialog.add(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void filterRadioButtonClicked(ActionEvent e) {
        exportStatisticsLabel.setText("Will export " + filteredEventsCount + " out of " + allEventsCount + " events.");
        exportStatisticsLabel.setVisible(true);
    }

    private void noFilterRadioButtonClicked(ActionEvent e) {
        exportStatisticsLabel.setText("Will export " + allEventsCount +"events.");
        exportStatisticsLabel.setVisible(true);
    }


    public boolean canExport(){
        return exportAllowed;
    }

    private void exportButtonClicked(ActionEvent e) {
        resultFilePath = textField.getText();
        if (resultFilePath.isEmpty()){
            JOptionPane.showMessageDialog(dialog, "Error: file path cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!resultFilePath.toLowerCase().endsWith(".json")) {
            resultFilePath += ".json";
        }
        exportAllowed = true;
        exportFiltered = filterRadioButton.isSelected();
        dialog.dispose();
    }

    private void openButtonClicked(ActionEvent e) {
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(dialog);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".json")) {
                filePath += ".json";
            }
            File file = new File(filePath);
            textField.setText(file.getAbsolutePath());
        } else {
            textField.setText("");
        }
    }

    public String getResultFilePath() {
        return resultFilePath;
    }

    public boolean getExportFiltered() {
        return exportFiltered;
    }
}