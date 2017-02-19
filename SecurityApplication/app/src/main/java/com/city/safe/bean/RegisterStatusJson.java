package com.city.safe.bean;

/**
 * Created by user on 16-12-27.
 */

public class RegisterStatusJson {

    /**
     * status : 1
     */

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RegisterStatusJson{" +
                "status=" + status +
                '}';
    }
}
