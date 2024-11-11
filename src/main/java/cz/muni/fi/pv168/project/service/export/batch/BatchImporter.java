package cz.muni.fi.pv168.project.service.export.batch;

import cz.muni.fi.pv168.project.service.export.format.FileFormat;

import java.io.IOException;
import java.util.Collection;

public interface BatchImporter extends FileFormat {
    Batch importBatch(String filePath) throws IOException;
}
