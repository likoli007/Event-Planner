package cz.muni.fi.pv168.project.business.service.export;

import cz.muni.fi.pv168.project.business.service.export.format.Format;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;

public interface ImportService {

    void importData(String filePath, JFrame frame) throws IOException;

    Collection<Format> getFormats();
}
