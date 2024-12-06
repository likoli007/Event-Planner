package cz.muni.fi.pv168.project.business.service.export;

import cz.muni.fi.pv168.project.business.service.export.format.Format;
import cz.muni.fi.pv168.project.model.DuplicateType;

import javax.swing.*;
import java.util.Collection;

public interface ImportService {

    boolean importData(String filePath, JFrame frame, DuplicateType defaultHandling) ;

    Collection<Format> getFormats();

    String getErrorMessage();
}
