package com.example.demo.exception;

import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/*
全局异常处理
* */
@Slf4j
@ControllerAdvice//控制器通知类
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public AppResult applicationExceptionHandler(ApplicationException e){
        //打印异常信息
        e.printStackTrace();
        //打印日志
        log.error(e.getMessage());
        if(e.getErrorResult()!=null){
            return e.getErrorResult();
        }
        //返回具体的异常信息
        if(e.getMessage()==null||e.getMessage().equals(" ")){
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
    return AppResult.failed(e.getMessage());
    }
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AppResult handleException (Exception e) {
        // 打印异常
        e.printStackTrace();
        // 记录日志
        log.error(e.getMessage());
        //判断非空
        if (e.getMessage() == null) {
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        return AppResult.failed(e.getMessage());
    }
}
