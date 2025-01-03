package cz.muni.fi.pv168.project.business.service.export;


import cz.muni.fi.pv168.project.business.service.export.format.Format;

import java.io.IOException;
import java.util.Collection;

/**
 * Generic mechanism, allowing to export data to a file.
 */
public interface ExportService {


    void exportData(String filePath, boolean exportFiltered) throws IOException;
    Collection<Format> getFormats();


}
