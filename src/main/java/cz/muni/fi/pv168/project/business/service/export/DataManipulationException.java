package cz.muni.fi.pv168.project.business.service.export;

public final class DataManipulationException extends RuntimeException {

    public DataManipulationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataManipulationException(String message) {
        super(message);
    }
}
