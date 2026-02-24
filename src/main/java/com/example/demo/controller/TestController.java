package com.example.demo.controller;

import com.example.demo.common.AppResult;
import com.example.demo.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@Api(tags = "测试类的相关接口")
@RestController
@RequestMapping("/test")
public class TestController {
    @ApiOperation("测试接口一，显示hello,soringboot")
    @GetMapping ("/hello")
    public String hello(){
        return "hello,springboot";
    }
    @ApiOperation("测试接口二，显示抛出一个异常")
    @GetMapping("/exception")
    public AppResult testException()throws Exception{
        throw new Exception("这是一个Exception异常");
    }
    @ApiOperation("测试接口三，显示抛出一个app异常")
    @GetMapping("/appException")
    public AppResult testapplicationException()throws ApplicationException{
        throw new ApplicationException("这是一个applicationException异常");
    }
    @ApiOperation("测试接口四，按传入的姓名显示你好信息")
    @PostMapping ("helloByName")
    public AppResult helloByName(@ApiParam("姓名") @RequestParam("name") String name){
        return AppResult.success("hello:"+name);
    }
}
