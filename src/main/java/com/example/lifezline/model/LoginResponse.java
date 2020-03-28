package com.example.lifezline.model;

public class LoginResponse<T> {
    private String code, description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private Object data;

    public LoginResponse(String code, String description, Object data){
        this.code = code;
        this.description = description;
        this.data = data;
    }
}
