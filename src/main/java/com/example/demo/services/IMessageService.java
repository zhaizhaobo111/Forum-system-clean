package com.example.demo.services;

import com.example.demo.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IMessageService {
    /**
     * 发送站内信
     * @param message 站内信对象
     */
    void create(Message message);
    /**
     * 根据用户id查询该用户未读数量
     * @param receiveUserId 用户id
     * @return 未读数量
     */
    Integer selectUnreadCount(Long receiveUserId);
    List<Message> selectByReceiveUserId(Long receiveUserId);

    /**
     * 更新指定站内信的状态
     * @param id 站内信id
     * @param state 目标状态
     */
    void updateStateById(Long id, Byte state);

    /**
     * 根据id查询站内信
     * @param id 站内信id
     * @return message
     */
    Message selectById(Long id);

    /**
     * 回复站内信
     * @param repliedId 要恢复的站内信id
     * @param message 回复的对象
     */
    @Transactional
    void reply(Long repliedId, Message message);
}
