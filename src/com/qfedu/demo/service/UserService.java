package com.qfedu.demo.service;

import com.qfedu.demo.dao.UserDao;
import com.qfedu.demo.model.User;
import com.qfedu.demo.utils.CommonsUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

public class UserService {

    UserDao userDao = new UserDao();

    public Integer login(String username, String password, HttpServletRequest req) {
        try {

            //校验验证码，注意验证码要先于用户名密码校验
            //获取登录请求传来的验证码
            String vcode = req.getParameter("vcode");
            HttpSession session = req.getSession();
            //获取 session 中保存的验证码
            String sessionVcode = (String) session.getAttribute("vcode");
            //用 equalsIgnoreCase 表示验证码不区分大小写
            if (vcode == null || !vcode.equalsIgnoreCase(sessionVcode)) {
                //说明用户传递的验证码不正确
                return CommonsUtils.BAD_VCODE;
            }

            User user = userDao.getUserByUsername(username);
            if (user == null) {
                //说明用户名不存在，登录失败
                return CommonsUtils.USERNAME_NOTFOUND_OR_BAD_PASSWORD;
            }else{
                //说明用户名存在
                if (!user.getEnabled()) {
                    //说明账户被禁用，不可用
                    return CommonsUtils.ACCOUNT_DISABLED;
                }else{
                    //账户没有被禁用，比对密码
                    if (password.equals(user.getPassword())) {
                        //密码相同，登录成功
                        //将用户信息存入 session
                        session.setAttribute("loginUser", user);
                        return CommonsUtils.LOGIN_SUCCESS;
                    }else{
                        //密码输入错误，登录失败
                        return CommonsUtils.USERNAME_NOTFOUND_OR_BAD_PASSWORD;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return CommonsUtils.OTHER_EXCEPTION;
    }
}
