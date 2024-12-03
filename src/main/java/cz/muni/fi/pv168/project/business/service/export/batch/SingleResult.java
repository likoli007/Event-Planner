package cz.muni.fi.pv168.project.business.service.export.batch;


public class SingleResult <T> {
    boolean success;
    String message;
    T data;

    public boolean isSuccess() {
        return success;
    }
    public T getData() {
        return data;
    }
    public SingleResult<T> setData(T data) {
        this.success = true;
        this.data = data;
        return this;
    }
    public String getMessage() {
        return message;
    }
    public SingleResult<T> setMessage(String message) {
        this.success = false;
        this.message = message;
        return this;
    }
}
