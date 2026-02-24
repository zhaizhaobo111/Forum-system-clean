package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;

public class AppResult<T> {
    //状态码
    @JsonInclude(JsonInclude.Include.ALWAYS) //不论任何情况下都参与 json 序列化
    private int code;
    //描述信息
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String message;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    //其他的数据
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public AppResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public AppResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;

    }
    /**
     * 成功
     *
     *
     */
    public static AppResult success() {
        return new AppResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }
    public static AppResult success(String message){
        return new AppResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }
    public static <T>AppResult<T> success(T data){
        return new AppResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),data);
    }
    public static <T>AppResult<T> success(String message,T data){
        return new AppResult<>(ResultCode.SUCCESS.getCode(), message,data);
    }
    /**
     * 失败
     *
     *
     */
    public static AppResult failed() {
        return new AppResult(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage());
    }

    public static <T> AppResult<T> failed(String message) {
        return new AppResult(ResultCode.FAILED.getCode(), message);
    }

    public static <T> AppResult<T> failed(T data) {
        return new AppResult(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), data);
    }
    public static AppResult failed(ResultCode resultCode) {
        return new AppResult(resultCode.getCode(), resultCode.getMessage());
    }
}
