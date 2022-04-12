package com.qfedu.demo.utils;

public interface CommonsUtils {
    //用户名不存在或者密码错误的异常
    Integer USERNAME_NOTFOUND_OR_BAD_PASSWORD = -1;
    //账户被禁用
    Integer ACCOUNT_DISABLED = -2;
    //登录成功
    Integer LOGIN_SUCCESS = -3;
    //其他异常
    Integer OTHER_EXCEPTION = -4;
    //验证码错误
    Integer BAD_VCODE = -5;
    Integer REPEATABLE_VALUE = -6;
    Integer INSERT_SUCCESS = -7;
    //无效的参数
    Integer INVALIDATE_PARAM = -8;
    Integer DELETE_SUCCESS = -9;
    Integer UPDATE_SUCCESS = -10;
}
