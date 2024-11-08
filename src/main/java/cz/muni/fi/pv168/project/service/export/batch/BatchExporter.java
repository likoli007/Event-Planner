package cz.muni.fi.pv168.project.service.export.batch;


import cz.muni.fi.pv168.project.service.export.format.FileFormat;

import java.io.IOException;

public interface BatchExporter extends FileFormat {

    void exportBatch(Batch batch, String filePath) throws IOException;
}