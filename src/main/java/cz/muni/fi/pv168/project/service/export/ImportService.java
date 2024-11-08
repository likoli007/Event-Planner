package cz.muni.fi.pv168.project.service.export;

import cz.muni.fi.pv168.project.service.export.format.Format;

import java.io.IOException;
import java.util.Collection;

public interface ImportService {

    void importData(String filePath) throws IOException;

    Collection<Format> getFormats();
}
