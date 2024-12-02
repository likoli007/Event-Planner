package cz.muni.fi.pv168.project.business.service.export.batch;

import cz.muni.fi.pv168.project.business.service.export.format.FileFormat;

import java.io.IOException;

public interface BatchImporter extends FileFormat {
    BatchResult importBatch(String filePath);
}
