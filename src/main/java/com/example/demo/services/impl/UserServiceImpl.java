package com.example.demo.services.impl;

import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.dao.UserMapper;
import com.example.demo.exception.ApplicationException;
import com.example.demo.model.User;
import com.example.demo.services.IUserService;
import com.example.demo.utils.MD5Util;
import com.example.demo.utils.StringUtil;
import com.example.demo.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import javax.annotation.Resource;

import java.util.Date;

import static com.example.demo.common.ResultCode.*;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {



    @Resource
    private UserMapper userMapper;
    @Override
    public void createNormalUser(User user) {
        //1.判断非空
        if(user==null|| StringUtil.isempty(user.getUsername()) ||
        StringUtil.isempty(user.getNickname())||StringUtil.isempty(user.getPassword())
        ||StringUtil.isempty(user.getSalt())){
            //打印警告
            log.warn(FAILED_PARAMS_VALIDATE.toString());
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2.按用户名查询用户信息
        User existsuser = userMapper.selectByUserName(user.getUsername());
        //判断用户是否为空
        if(existsuser!=null){
            log.info( FAILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed( FAILED_USER_EXISTS));
        }
        //新增用户流程
        user.setGender((byte) 2);
        user.setIsAdmin((byte) 0);
        user.setAvatarUrl(null);
        user.setArticleCount(0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        //导入数据库
        int row=userMapper.insertSelective(user);
        if(row!=1){
            log.info(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(FAILED_CREATE));
        }
        log.info("新增成功.username"+user.getUsername()+".");
    }
    @Override
    /**
     * 按用户名查询信息
     */
    public User selectByUserName(String username) {
        //非空判断
        if(StringUtil.isempty(username)){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //返回结果
        return userMapper.selectByUserName(username);
    }

    @Override
    /**
     * 登录校验
     */
    public User loginin(String uername, String password) {
        //1.非空校验
        if(StringUtil.isempty(uername)||StringUtil.isempty(password)){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //校验失败
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //2.按用户名查询用户信息
        User user = selectByUserName(uername);
        //3.对查询结果进行非空校验
        if(user==null){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //校验失败
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //4.对密码进行校验
        String encryptPassword = MD5Util.md5salt(password, user.getSalt());
        //5.和数据库的密文进行校验
        if(!encryptPassword.equalsIgnoreCase(user.getPassword())){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString()+"密码错误， username=="+uername);
            //校验失败
            throw new ApplicationException(AppResult.failed(FAILED_LOGIN));
        }
        //登录成功，返回用户信息
        return user;
    }

    @Override
    public User selectById(Long id) {
        if(id==null){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //校验失败
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        /*
        * 调用dao查询数据库获取对象
        * */
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if(id==null||id<=0){
            log.info(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT));
        }
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null){
            log.warn(ResultCode.ERROR_IS_NULL.toString()+" board id:"+id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新用户的发帖数量
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount()+1);
        int row =userMapper.updateByPrimaryKeySelective(updateUser);
        if(row!=1){
            log.warn(ResultCode.FAILED.toString()+"受影响的行数不等于1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        if(id==null||id<=0){
            log.info(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BOARD_APRICLR_COUNT));
        }
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null){
            log.warn(ResultCode.ERROR_IS_NULL.toString()+" board id:"+id);
            //抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新用户的发帖数量
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount()-1);
        if (updateUser.getArticleCount()<0) {
            updateUser.setArticleCount(0);
        }
        int row =userMapper.updateByPrimaryKeySelective(updateUser);
        if(row!=1){
            log.warn(ResultCode.FAILED.toString()+"受影响的行数不等于1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyInfo(User user) {
        if(user==null||user.getId()<=0){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if(existsUser==null){
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        //定义标志位,校验所有参数同时为空
        boolean checkAttr=false;
     //定义一个专门用来更新的对象，防止用户传入的user对象设置了其他属性操作
        //当使用动态sql进行更新的时候，覆盖了没有经过校验的字段
        User updataUser=new User();
        //设置用户id
        updataUser.setId(user.getId());
        //对每个参数进行校验并赋值
        if(!StringUtil.isempty(user.getUsername())
          &&!user.getUsername().equals(existsUser.getUsername())){//进行校验，用户名唯一
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if(checkUser!=null){
                //用户已存在
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
            }
            //数据库没有找到想应的用户，可以修改用户名
            updataUser.setUsername(user.getUsername());
            checkAttr=true;
        }
        if(!StringUtil.isempty(user.getNickname())
                &&!user.getNickname().equals(existsUser.getNickname())){
            updataUser.setNickname(user.getNickname());
            checkAttr=true;
        }
        if(user.getGender()!=null&&user.getGender()!=existsUser.getGender()){
            updataUser.setGender(user.getGender());
            if(updataUser.getGender()>2||updataUser.getGender()<0){
                updataUser.setGender((byte) 2);
            }
            checkAttr=true;
        }
        if(!StringUtil.isempty(user.getEmail())
                &&!user.getEmail().equals(existsUser.getEmail())){
            updataUser.setEmail(user.getEmail());
            checkAttr=true;
        }
        if(!StringUtil.isempty(user.getPhoneNum())
                &&!user.getPhoneNum().equals(existsUser.getPhoneNum())){
            updataUser.setPhoneNum(user.getPhoneNum());
            checkAttr=true;
        }
        if(!StringUtil.isempty(user.getRemark())
                &&!user.getRemark().equals(existsUser.getRemark())){
            updataUser.setRemark(user.getRemark());
            checkAttr=true;
        }
        if(checkAttr==false){
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        int row = userMapper.updateByPrimaryKeySelective(updataUser);
        if(row!=1){
            log.warn(ResultCode.FAILED.toString()+"受影响的行数不等于1");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyPassword(Long id, String newPassword, String oldPassword) {
        // 1. 根据用户Id查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        // 2. 校验用户Id是否有存在
        if (user == null) {
        // 记录日志
            log.info(ResultCode.FAILED_UNAUTHORIZED.toString() + "user id = " +
                    id);
        // 抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_UNAUTHORIZED));
        }
        // 3. 校验原密码是否正确
        String oldEncryptPassword = MD5Util.md5salt(oldPassword, user.getSalt());
        if (!oldEncryptPassword.equalsIgnoreCase(user.getPassword())) {
        // 记录日志
            log.info(ResultCode.FAILED_PARAMS_VALIDATE.toString() + "username = "
                    + user.getUsername() + ", password = " + oldPassword);
        // 密码不正确抛出异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 4. 重新生成扰动字符串
        String salt = UUIDUtil.uuid_32();
        // 5. 计算新密码
        String encryptPassword = MD5Util.md5salt(newPassword, salt);
        // 6. 创建用于更新的User对象
        User modifyUser = new User();
        // 设置用户Id
        modifyUser.setId(user.getId());
        // 设置新密码
        modifyUser.setPassword(encryptPassword);
        // 设置扰动字符串
        modifyUser.setSalt(salt);
        // 设置更新时间
        modifyUser.setUpdateTime(new Date());
        // 7. 更新操作
        userMapper.updateByPrimaryKeySelective(modifyUser);
        log.info("用户密码修改成功：" + user.getUsername());
    }

    @Override
    public void updateAvatarUrl(Long id, String avatarUrl) {
        // 1. 根据用户Id查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        // 2. 校验用户Id是否有存在
        if (user == null) {
            log.info(ResultCode.FAILED_UNAUTHORIZED.toString() + "user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_UNAUTHORIZED));
        }
        // 3. 创建用于更新的User对象
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setAvatarUrl(avatarUrl);
        updateUser.setUpdateTime(new Date());
        // 4. 更新操作
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn("更新头像失败：userId={}", id);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
        log.info("头像更新成功：userId={}", id);
    }
}
