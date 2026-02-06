package com.fantasy.fm.constant;

public class AuthConstant {
    public static final String USER_ID = "userId";
    public static final String EMAIL_REGEX =
            "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    public static final String USERNAME_REGEX = "^(?!\\d+$).{3,}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z]).{8,24}$";


    public static final String USER_NOT_FOUND = "用户不存在!";
    public static final String USER_ALREADY_EXISTS = "用户已存在!";
    public static final String USERNAME_INVALID = "用户名无效!";
    public static final String INVALID_PASSWORD = "密码无效!";
    public static final String INVALID_EMAIL = "邮箱格式不合法!";
    public static final String NEW_PASSWORD_OLD_SAME = "新密码不能与旧密码相同!";
    public static final String ERROR_PASSWORD = "密码错误!";
    public static final String LOGOUT_SUCCESS = "退出登录成功!";
    public static final String LOGIN_EXCEPTION = "登录遇到问题,请稍后再试!";
    public static final String EMAIL_INVALID = "邮箱格式不正确";
    public static final String CODE_SEND_FAIL = "验证码发送失败，请稍后重试";
    public static final String NEED_CAPTCHA = "检测到异常登录，请输入验证码";
    public static final String CODE_INVALID = "验证码错误或已失效";
    public static final String INPUT_EMPTY = "邮箱和验证码不能为空";
    public static final String CODE_WRONG = "验证码错误或失效,请尝试重新获取";
    public static final String EMAIL_NOT_ALLOW_EMPTY = "邮箱不能为空,否则无法找回账号";
    public static final String FAKE_CODE_SEND_MESSAGE = "验证码已发送!";
    public static final String REAL_CODE_SEND_MESSAGE = "验证码发送成功，请注意查收！";
    public static final String USER_VALIDATION_FAILED = "用户校验失败!";
}
