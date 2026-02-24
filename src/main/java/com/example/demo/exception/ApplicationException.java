package com.example.demo.exception;

import com.example.demo.common.AppResult;
//自定义异常
public class ApplicationException extends RuntimeException{
    // 在异常中的错误对象
    protected AppResult errorResult;
    public ApplicationException(AppResult errorResult){
        super(errorResult.getMessage());
        this.errorResult=errorResult;
    }

    public AppResult getErrorResult() {
        return errorResult;
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
