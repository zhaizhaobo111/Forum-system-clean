package com.example.demo.controller;

import com.example.demo.common.AppCpnfig;
import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.services.IMessageService;
import com.example.demo.services.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Api(tags = "站内信接口")
@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private IMessageService messageService;
    @Resource
    private IUserService userService;
    @ApiOperation("发送站内信")
    @PostMapping("/send")
    public AppResult send(HttpServletRequest request,
                          @ApiParam(value = "接收用户Id")@RequestParam("receiveUserId") @NonNull Long receiveUserId,
                          @ApiParam(value = "站内信内容")@RequestParam("content") @NonNull String content){

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        //判断用户是否禁言
        if(user.getState()==1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //不能给自己发消息
        if(user.getId()==receiveUserId){
            return AppResult.failed("不能给自己发站内信");
        }
        //检验接收者是否存在
        User receiveUser = userService.selectById(receiveUserId);
        if(receiveUser==null||receiveUser.getDeleteState()==1){
            return AppResult.failed("接收者状态异常");
        }
        //封装对象
        Message message =new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(receiveUserId);
        message.setContent(content);
        //调用service
        messageService.create(message);
        return  AppResult.success("发送成功");
    }
    @ApiOperation("获取未读数量")
    @GetMapping("/getUnreadCount")
    public AppResult<Integer>getUnreadCount(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        Integer count = messageService.selectUnreadCount(user.getId());//当前登录id
        return AppResult.success(count);
    }
    @ApiOperation("查询用户的所有站内信")
    @GetMapping("getAll")
    public AppResult<List<Message>> getAll(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());
        return AppResult.success(messages);
    }
    @ApiOperation("更新为已读")
    @PostMapping("/markRead")
    public AppResult markRead(HttpServletRequest request,@ApiParam("站内信id")@RequestParam("id")@NonNull Long id){
        //根据id查询站内信
        Message message = messageService.selectById(id);
        //站内信是否存在
        if(message==null||message.getDeleteState()==1){
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        //站内信是不是自己的
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        if(user.getId()!=message.getReceiveUserId()){
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }
        //dao
        messageService.updateStateById(id, (byte) 1);
        return AppResult.success();
    }
    @ApiOperation("回复站内信")
    @PostMapping("reply")
    public AppResult reply(HttpServletRequest request,
                           @ApiParam("要回复的站内信")@RequestParam("repliedId") @NonNull Long repliedId,
                           @ApiParam("站内信的内容")@RequestParam("content") @NonNull String content){
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        if(user.getState()==1){
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }
        //不能给自己回复
        Message existsMessage=messageService.selectById(repliedId);
        if(existsMessage==null||existsMessage.getDeleteState()==1){
            return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
        }
        //不能给自己回复
        if(user.getId()==existsMessage.getPostUserId()){
            return AppResult.failed("不能给自己的站内信回复");
        }
        //构造对象
        Message message=new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(existsMessage.getPostUserId());
        message.setContent(content);
        messageService.reply(repliedId, message);
        return AppResult.success();

    }
}
