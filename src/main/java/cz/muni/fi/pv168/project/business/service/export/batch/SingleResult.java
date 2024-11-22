package cz.muni.fi.pv168.project.business.service.export.batch;


public class SingleResult <T> {
    boolean success;
    String message;
    T data;

    public SingleResult() {}

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean sucess) {
        this.success = sucess;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
