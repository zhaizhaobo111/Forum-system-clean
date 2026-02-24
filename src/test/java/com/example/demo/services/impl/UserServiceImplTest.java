package com.example.demo.services.impl;

import com.example.demo.model.User;
import com.example.demo.services.IUserService;
import com.example.demo.utils.MD5Util;
import com.example.demo.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {
   /*
   @Transactional 在测试方法执行之后会发生事务回滚
   * */
    @Test
    void selectByUserName() {
        System.out.println(userService.selectByUserName("username"));
    }

    @Test
    void loginin() {
        System.out.println(userService.loginin("username", "123456"));
    }
   @Resource
    private IUserService userService;

    @Test
    void selectById() {
        User user = userService.selectById(2l);
        System.out.println(user);
    }

    @Test
    @Transactional//回滚
    void addOneArticleCountById() {
        userService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void subOneArticleCountById() {
        userService.subOneArticleCountById(2l);
        System.out.println("删除帖子成功");
    }

    @Test
    @Transactional
    void createNormalUser() {
        User user=new User();
        user.setUsername("username");
        user.setNickname("nickname");
        //设置明文密码
        String password="123456";
        //设置盐
        String salt= UUIDUtil.uuid_32();
        //生成密码的密文
        String ciphertext= MD5Util.md5salt(password,salt);
        //设置加密后的密码
        user.setPassword(ciphertext);
        //设置盐
        user.setSalt(salt);
        userService.createNormalUser(user);
    }

    @Test
    void modifyInfo() {
        User user=new User();
        user.setId(3l);
        user.setGender((byte) 0);
        user.setEmail("123@qq.com");
        user.setNickname("testUser1");
        user.setUsername("testUser111");
        user.setPhoneNum("1536668888");
        user.setRemark("测试");
        userService.modifyInfo(user);
    }

    @Test
    @Transactional
    void modifyPassword() {
        userService.modifyPassword(2l,"111111","123456");
        System.out.println("更新成功");
    }
}