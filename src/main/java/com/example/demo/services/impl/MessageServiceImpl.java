package com.example.demo.services.impl;

import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.dao.MessageMapper;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.services.IMessageService;
import com.example.demo.services.IUserService;
import com.example.demo.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {
   @Resource
    private MessageMapper messageMapper;
   @Resource
    private IUserService userService;
    @Override
    public void create(Message message) {
        if(message==null||message.getPostUserId()==null
        ||message.getReceiveUserId()==null|| StringUtil.isempty(message.getContent())){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }//检验接收者是否存在
        User user = userService.selectById(message.getReceiveUserId());
        if(user==null||user.getDeleteState()==1){
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        message.setState((byte) 0);
        message.setDeleteState((byte) 0);
        Date date=new Date();
        message.setCreateTime(date);
        message.setUpdateTime(date);
        int row = messageMapper.insertSelective(message);
        if(row!=1){
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        if(receiveUserId==null||receiveUserId<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用dao
        Integer count = messageMapper.selectUnreadCount(receiveUserId);
        if(count==null){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        return count;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {
        if(receiveUserId==null||receiveUserId<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);

        return messages;
    }

    @Override
    public void updateStateById(Long id, Byte state) {
        //0表示未读，1表示已读，2表示已回复
        if(id==null||id<=0||state<0||state>2){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Message message=new Message();
        message.setId(id);
        message.setState(state);
        Date date=new Date();
        message.setUpdateTime(date);
        int row = messageMapper.updateByPrimaryKeySelective(message);
        if(row!=1){
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public Message selectById(Long id) {
        if(id==null||id<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用dao
        Message message = messageMapper.selectByPrimaryKey(id);
        //返回结果
        return message;
    }

    @Override
    public void reply(Long repliedId, Message message) {
        if(repliedId==null||repliedId<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //检验repliedId对应的站内信状态
        Message existsMessage = messageMapper.selectByPrimaryKey(repliedId);
        if(existsMessage==null||existsMessage.getDeleteState()==1){
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS));
        }
        //更新状态为已回复
       updateStateById(repliedId, (byte) 2);
        create(message);

    }
}
