package com.example.demo.services;

import com.example.demo.model.Board;
import com.example.demo.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *用户接口
 */
public interface IUserService {
    /**
     * 创建一个用户
     * @param user 用户信息
     */
    void createNormalUser(User user);

    /**
     * 根据用户名提供信息
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUserName (String username);

    /**
     * 处理用户登录
     * @param uername 用户名
     * @param password 用户密码
     * @return 用户信息
     */
    User loginin(String uername,String password);

    /**
     * 根据用户id查询用户信息
     * @param id 用户id
     * @return 用户信息
     */
    User selectById(Long id);

    /**
     * 更新用户发帖中的帖子数量-1
     * @param id 用户id
     */
    void addOneArticleCountById(Long id);

    /**
     * 更新用户发帖中的帖子数量-1
     * @param id 用户id
     */
    void subOneArticleCountById(Long id);

    /**
     * 修改个人信息
     * @param user 要修改的个人信息
     */
    void modifyInfo(User user);

    /**
     * 修改密码
     * @param id 用户id
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    void modifyPassword(Long id,String newPassword, String oldPassword);

    /**
     * 更新用户头像URL
     * @param id 用户id
     * @param avatarUrl 头像URL
     */
    void updateAvatarUrl(Long id, String avatarUrl);
}
