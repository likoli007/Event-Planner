package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.model.DuplicateType;

import javax.swing.*;
import java.util.Collection;

/**
 * Generic mechanism, allowing to import data from a file.
 */
public interface Importer {

    /**
     * Imports data from a file.
     *
     * @param filePath        absolute path of the import file
     * @param frame           frame to draw dialog to
     * @param defaultHandling default way of treating duplicates
     */
    void importData(String filePath, JFrame frame, DuplicateType defaultHandling);

    /**
     * Gets all available formats for import.
     */
    Collection<Format> getFormats();
}
