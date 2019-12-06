package com.nontivi.nonton.data.response;

public class MetaResponse<T> {
    private int status;
    private String message;
    private T data;
    private T error;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getError() {
        return error;
    }

    public void setError(T error) {
        this.error = error;
    }

    public boolean isRequestError() {
        return status / 100 == 4;
    }
}
