package cz.muni.fi.pv168.project.storage.sql.dao;

import java.io.Serial;

/**
 * Exception that is thrown if there is some problem with data storage
 */
public class DataStorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 0L;

    public DataStorageException(String message) {
        this(message, null);
    }

    public DataStorageException(String message, Throwable cause) {
        super("Storage error: " +  message, cause);
    }
}

