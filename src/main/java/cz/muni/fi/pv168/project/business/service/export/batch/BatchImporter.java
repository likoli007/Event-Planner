package cz.muni.fi.pv168.project.business.service.export.batch;

import cz.muni.fi.pv168.project.business.service.export.format.FileFormat;

public interface BatchImporter extends FileFormat {
    SingleResult<Batch> importBatch(String filePath);
}
