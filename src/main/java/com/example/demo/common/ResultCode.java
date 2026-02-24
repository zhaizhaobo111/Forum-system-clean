package com.example.demo.common;

public enum ResultCode {
    /** 定义状态码 */
    SUCCESS (0, "成功"),
    FAILED (1000, "操作失败"),
    FAILED_UNAUTHORIZED (1001, "未授权"),
    FAILED_PARAMS_VALIDATE (1002, "参数校验失败"),
    FAILED_FORBIDDEN (1003, "禁止访问"),
    FAILED_CREATE (1004, "新增失败"),

    FAILED_USER_EXISTS (1101, "用户已存在"),
    FAILED_USER_NOT_EXISTS (1102, "用户不存在"),
    FAILED_NOT_EXISTS (1005, "资源不存在"),
    FAILED_LOGIN (1103, "用户名或密码错误"),
    FAILED_USER_BANNED (1104, "您已被禁言, 请联系管理员, 并重新登录."),
    //关于板块的错误描述
    FAILED_USER_BOARD_APRICLR_COUNT(1201,"更新帖子数量失败"),
    FAILED_BOARD_BANNED(1202,"板块状态异常"),
    FAILED_BOARD_NOT_EXISTS (1203, "板块不存在"),
    FAILED_ARTICLE_NOT_EXISTS (1301, "帖子不存在"),
    FAILED_ARTICLE_BANNED (1302, "帖子状态异常"),
    FAILED_MESSAGE_NOT_EXISTS (1401, "站内信不存在"),
    FAILED_TWO_PWD_NOT_SAME (1105, "两次输入的密码不一致"),
    ERROR_SERVICES (2000, "服务器内部错误"),
    ERROR_IS_NULL (2001, "IS NULL.");
    //状态码
    int code;
    //描述信息
    String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "code = " + code + ", message = " + message + ". ";
    }
}
