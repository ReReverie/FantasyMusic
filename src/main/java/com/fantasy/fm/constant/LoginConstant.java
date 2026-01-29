package com.fantasy.fm.constant;

public class LoginConstant {
    public static final String USER_ID = "userId";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String USERNAME_REGEX = "^(?!\\d+$).{3,}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{8,24}$";


    public static final String USER_NOT_FOUND = "用户不存在!";
    public static final String USER_ALREADY_EXISTS = "用户已存在!";
    public static final String USERNAME_INVALID = "用户名无效!";
    public static final String INVALID_PASSWORD = "密码无效!";
    public static final String NEW_PASSWORD_OLD_SAME = "新密码不能与旧密码相同!";
    public static final String ERROR_PASSWORD = "密码错误!";
    public static final String LOGOUT_SUCCESS = "退出登录成功!";
}
