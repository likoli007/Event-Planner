package cz.muni.fi.pv168.project.business.service.export.batch;

public class BatchResult {
    private boolean success;
    private String message;
    Batch batch;
    public BatchResult() {}

    public BatchResult(boolean success, String message, Batch batch) {
        this.success = success;
        this.message = message;
        this.batch = batch;
    }

    public BatchResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BatchResult(boolean success, Batch batch) {
        this.success = success;
        this.batch = batch;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public Batch getBatch() {
        return batch;
    }

    public String getMessage() {
        return message;
    }

    public boolean succeeded() {
        return success;
    }
}
