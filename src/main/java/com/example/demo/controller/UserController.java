package com.example.demo.controller;

import com.example.demo.common.AppCpnfig;
import com.example.demo.common.AppResult;
import com.example.demo.common.ResultCode;
import com.example.demo.model.Board;
import com.example.demo.model.User;
import com.example.demo.services.IUserService;
import com.example.demo.utils.MD5Util;
import com.example.demo.utils.StringUtil;
import com.example.demo.utils.UUIDUtil;
import com.mysql.cj.Session;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.LongHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*
* 返回数据的Controller
* */
//对Controller进行说明
@Api(tags="用户接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;
    /**
     *
     * @param username 用户名
     * @param nickname 昵称
     * @param password 用户密码
     * @param passwordRepeat 重复密码
     * @return
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NonNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NonNull String nickname,
                              @ApiParam("密码") @RequestParam("password") @NonNull String password,
                              @ApiParam("重复密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat){
        if(StringUtil.isempty(username) ||StringUtil.isempty(password) ||
            StringUtil.isempty(nickname)||StringUtil.isempty(passwordRepeat))
            //返回信息 参数校验失败
            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);

        //校验密码和重复密码是否相同
        if(!password.equals(passwordRepeat)){
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        //准备数据
        User user=new User();
        user.setUsername(username);
        user.setNickname(nickname);
        //生成加密密码
        //生成盐
        String salt= UUIDUtil.uuid_32();
        //生成加密密文
        String encryptPassword = MD5Util.md5salt(password, salt);
        //设置密码
        user.setPassword(encryptPassword);
        //设置盐
        user.setSalt(salt);

        //调用userservice
        userService.createNormalUser(user);
        return AppResult.success();

    }
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request ,
                           @ApiParam("用户名") @RequestParam("username") @NonNull String username,
                           @ApiParam("密码") @RequestParam("password") @NonNull String password){
        //1.调用用service的登陆方法，返回user对象
        User user = userService.loginin(username, password);
        if(user==null){
            //打印日志
            log.warn(ResultCode.FAILED_LOGIN.toString());
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        //2.如果登录成功把user对象设置到session作用域
        HttpSession session = request.getSession(true);
        session.setAttribute(AppCpnfig.USER_SESSION,user);
        log.info("登录成功");
        //3.返回结果
        return AppResult.success();
    }
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @ApiParam("用户id")@RequestParam(value = "id",required = false) Long id){
        //全局定义user反回对象
        User user=null;
        //根据id获取用户信息
        if(id==null){
            //1.如果id位空，从session中获取当前登录的用户信息
            HttpSession session = request.getSession(false);
            //判断session和用户信息是否有效
            if(session==null||session.getAttribute(AppCpnfig.USER_SESSION)==null){
                //用户没有登录,返回错误信息
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            user= (User) session.getAttribute(AppCpnfig.USER_SESSION);
        }else {
            //2.如果id不为空，从数据库中按id查询用户信息
            user = userService.selectById(id);
        }
        //判断用户对象是否为空
        if(user==null){
        return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        //返回正常结果
        return AppResult.success(user);
    }
    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public AppResult logout(HttpServletRequest request){
        //获取session对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session!=null){
            //打印日志
            log.info("退出成功");
            //表示用户在登陆状态，直接销毁session
            session.invalidate();
        }
        return AppResult.success("退出成功");
    }

    /**
     *修改个人信息
     * @param request
     * @param username 用户名
     * @param nickname 昵称
     * @param gender 性别
     * @param email 邮箱
     * @param phoneNum 电话号码
     * @param remark 个人简介
     * @return
     */
    @ApiOperation("修改个人信息")
    @PostMapping("modifyInfo")
    public AppResult modifyInfo(HttpServletRequest request,
                                @ApiParam("用户名")@RequestParam(value = "username",required = false)  String username,
                                @ApiParam("昵称")@RequestParam(value = "nickname",required = false) String nickname,
                                @ApiParam("性别")@RequestParam(value = "gender",required = false) Byte gender,
                                @ApiParam("邮箱")@RequestParam(value = "email",required = false)String email,
                                @ApiParam("电话号码")@RequestParam(value = "phoneNum",required = false) String phoneNum,
                                @ApiParam("个人简介")@RequestParam(value = "remark",required = false) String remark){
        // 1. 接收参数
        // 2. 对参数做非空校验（全部都为空，则返回错误描述）
        if (StringUtil.isempty(username) && StringUtil.isempty(nickname)
                && StringUtil.isempty(email) && StringUtil.isempty(phoneNum)
                && StringUtil.isempty(remark) && gender == null) {
        // 返回错误信息
            return AppResult.failed("请输入要修改的内容");
        }
        // 从session中获取用户Id
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        // 3. 封装对象
        User updateUser = new User();
        updateUser.setId(user.getId()); // 用户Id
        updateUser.setUsername(username); // 用户名
        updateUser.setNickname(nickname); // 昵称
        updateUser.setGender(gender); // 性别
        updateUser.setEmail(email); // 邮箱
        updateUser.setPhoneNum(phoneNum); // 电话
        updateUser.setRemark(remark); // 个人简介
        // 4. 调用Service中的方法
        userService.modifyInfo(updateUser);
        // 5. 查询最新的用户信息
        user = userService.selectById(user.getId());
        // 6. 把最新的用户信息设置到session中
        session.setAttribute(AppCpnfig.USER_SESSION, user);
        // 7. 返回结果
        return AppResult.success(user);
    }
    @ApiOperation("修改密码")
    @PostMapping("/modifyPwd")
    public AppResult modifyPassword (HttpServletRequest request,
                                     @ApiParam("原密码") @RequestParam("oldPassword") @NonNull String oldPassword,
                                     @ApiParam("新密码") @RequestParam("newPassword") @NonNull String newPassword,
                                     @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        // 1. 校验新密码与确认密码是否相同
        if (!newPassword.equals(passwordRepeat)) {
        // 返回错误描述
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        // 2. 获取当前登录的用户信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppCpnfig.USER_SESSION);
        // 3. 调用Service
        userService.modifyPassword(user.getId(), newPassword, oldPassword);
        // 4. 销毁session
        if (session != null) {
            session.invalidate();
        }
        // 5. 返回结果
        return AppResult.success();
    }

}
